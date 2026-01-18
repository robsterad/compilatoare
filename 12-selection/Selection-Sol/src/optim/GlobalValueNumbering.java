package optim;

import analysis.DominatorTree;
import irgen.CFG;
import irgen.BasicBlock;

import java.util.*;
import java.util.stream.Collectors;

import irgen.Instruction;
import irgen.InstructionType;
import semantic.Symbol;

/**
 * Extensia strategiei Value Numbering la nivelul unui întreg CFG, deja transformat în forma SSA.
 *
 * Modalitatea de propagare a analizei în CFG mimează pașii (1)-(5) descriși pentru etapa (2), de redenumire,
 * a transformării în forma SSA. Vezi comentariile metodei rename(BasicBlock) din clasa SSATransformer.
 */
public class GlobalValueNumbering extends ValueNumbering {
    /**
     * CFG-ul de optimizat
     */
    CFG cfg;

    /**
     * Arborele de dominare, necesar propagării analizei
     */
    DominatorTree dominatorTree;

    /**
     * Reținem dacă s-a modificat ceva după rularea optimizării
     */
    boolean changed;

    public GlobalValueNumbering(CFG cfg) {
        this.cfg = cfg;

        // Dacă CFG-ul corespunde unei funcții, parametrii formali trebuie adăugați în tabel la început pentru a fi
        // accesibili într-o manieră uniformă pe toate căile CFG-ului
        var functionSymbol = cfg.functionSymbol;
        if (functionSymbol != null)
            functionSymbol.symbols.values().forEach(param -> getValueNumber(param, param));
    }

    /**
     * Propagarea începe cu blocul de intrare în CFG și continuă recursiv conform unei parcurgeri în preordine
     * a arborelui de dominanță.
     *
     * @return true, dacă s-a modificat vreo instrucțiune
     */
    public boolean run() {
        // Arborele de dominanță este determinat la începutul fiecărei runde, întrucât este posibil ca CFG-ul să
        // se fi modificat între timp (de exemplu, după eliminarea codului neexecutabil)
        dominatorTree = new DominatorTree(cfg);

        // Presupunem că nu se va modifica nimic
        changed = false;

        // Începem analiza cu blocul de intrare
        run(cfg.blocks.getFirst());

        return changed;
    }

    /**
     * Suprascrie metoda run(BasicBlock) din clasa părinte, astfel încât, pe lângă prelucrarea tuturor instrucțiunilor
     * din bloc, să și propage analiza în subarborele de dominanță cu rădăcina în acest bloc.
     *
     * Modalitatea de propagare a analizei în CFG mimează pașii (1)-(5) descriși pentru etapa (2), de redenumire,
     * a transformării în forma SSA. Vezi comentariile metodei rename(BasicBlock) din clasa SSATransformer.
     *
     * @param block blocul curent
     */
    @Override
    public void run(BasicBlock block) {
        // La intrarea într-un bloc, se salvează conținutul curent al tabelului, întrucât noile intrări vor fi
        // înlăturate (vezi explicațiile de la finalul metodei)
        var savedValueNumbers = new LinkedHashMap<>(valueNumbers);

        // Analog pașilor (1)-(2) menționați mai sus: Prelucrarea instrucțiunilor blocului curent, ϕ și de alte tipuri.
        // Implementarea moștenită doar prelucrează instrucțiunile.
        super.run(block);

        // Analog pasului (3) menționat mai sus: Popularea argumentelor instrucțiunilor ϕ din fiecare succesor din CFG
        // al blocului curent
        for (var successor : block.successors) {
            for (var instruction : successor.instructions) {
                if (instruction.iType != InstructionType.PHI)
                    continue;

                var predecessorIndex = successor.predecessors.indexOf(block);
                var valueNumber = getValueNumber(instruction.operands.get(predecessorIndex), null);
                var operand = instruction.operands.set(predecessorIndex, valueNumber);

                // Dacă am propagat un număr de valoare diferit de vechiul operand, marcăm modificarea
                if (operand != valueNumber)
                    changed = true;
            }
        }

        // Analog pasului (4) menționat mai sus: Propagarea recursivă către copiii din arborele de dominanță,
        // i.e. către blocurile dominate direct (parcurgere în preordine, pentru că am prelucrat deja blocul curent)
        dominatorTree.childrenSets.get(block).forEach(this::run);

        // Analog pasului (5): Eliminarea din tabel a intrărilor introduse în cadrul acestui subarbore de dominanță
        valueNumbers = savedValueNumbers;
    }

    /**
     * Analizează și eventual rescrie o instrucțiune, adăugând intrări noi în tabelul valorilor.
     *
     * Față de implementarea din clasa ValueNumbering discutată în laboratorul 9, se adaugă un tratament special
     * al instrucțiunilor ϕ, astfel încât, dacă toți operanzii non-bloc ai acesteia coincid, instrucțiunea ϕ
     * poate fi înlocuită cu una de copiere a unui operand. De exemplu, a = ϕ b b bloc0 bloc1 poate fi înlocuită
     * cu a = COPY b.
     *
     * @param instruction instrucțiunea curentă
     */
    @Override
    protected void run(Instruction instruction) {
        // Reținem textul curent al instrucțiunii, pentru a verifica la final dacă s-a modificat
        var originalInstructionText = instruction.toString();

        // Față de implementarea moștenită din clasa părinte, trebuie să tratăm suplimentar instrucțiunile ϕ
        if (instruction.iType == InstructionType.PHI) {
            // TODO Verificăm dacă toți operanzii valoare ai instrucțiunii ϕ sunt identici. Ne uităm la prima
            //  jumătate a operanzilor, întrucât cealaltă jumătate sunt blocuri.
            var firstOperand = instruction.operands.getFirst();
            var numPreds = instruction.operands.size() / 2;
            var cond = instruction.operands.subList(1, numPreds).stream()
                    .allMatch(firstOperand::equals);

            // TODO Dacă este îndeplinită condiția de mai sus, schimbăm tipul instrucțiunii la COPY, iar unicul
            //  operand este oricare dintre operanzii anteriori
            if (cond) {
                instruction.iType = InstructionType.COPY;
                instruction.operands = Collections.singletonList(firstOperand);
            }
            // TODO Dacă nu, putem totuși verifica dacă nu cumva există deja o instrucțiune ϕ cu aceiași parametri,
            //  valori și blocuri (common subexpression elimination)
            else {
                // TODO Construim cheia formată din operația ϕ, numerele valorilor operanzilor valoare (prima jumătate
                //  a operanzilor) și numele operanzilor bloc. Exemplu: ϕ a.0 2 bloc0 bloc1.
                //  Dacă expresia a fost deja evaluată, numărul valorii este cel stocat în tabel.
                //  Altfel, este rezultatul curent.
                var key = instruction.iType
                        + " "
                        + instruction.operands.subList(0, numPreds).stream()
                                .map(operand -> getValueNumber(operand, operand).getDisplayName())
                                .collect(Collectors.joining(" "))
                        + " "
                        + instruction.operands.subList(numPreds, 2 * numPreds).stream()
                                .map(Symbol::getDisplayName)
                                .collect(Collectors.joining(" "));

                // TODO Determinăm numărul valorii cheii
                var keyValueNumber = getValueNumber(key, instruction);

                // TODO Common subexpression elimination: numărul valorii cheii constituie o intrare anterioară din tabel
                //  (expresia a fost evaluată anterior)
                //  Completați condiția if-ului
                if (keyValueNumber != instruction) {
                    // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi, care
                    //  conține acum o singură intrare, cu rezultatul calculat anterior
                    instruction.iType = InstructionType.COPY;
                    instruction.operands = Collections.singletonList(keyValueNumber);
                }

                // TODO Indiferent dacă expresia a fost evaluată anterior sau nu, rezultatul operației trebuie să aibă
                //  același număr de valoare cu cheia expresiei
                valueNumbers.put(instruction, keyValueNumber);
            }
        }

        // Dacă instrucțiunea curentă nu este ϕ (posibil să fi fost și să se fi transformat într-una de copiere), rulăm
        // și implementarea moștenită
        if (instruction.iType != InstructionType.PHI)
            super.run(instruction);

        // Verificăm dacă instrucțiunea curentă s-a modificat
        if (! instruction.toString().equals(originalInstructionText))
            changed = true;
    }
}
