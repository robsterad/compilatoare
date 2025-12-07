package optim;

import irgen.*;
import semantic.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Strategia Value Numbering, asumând forma SSA a codului intermediar.
 *
 * Principiul este de a asocia o valoare abstractă, caracterizată de un anumit număr (value number), tuturor expresiilor
 * care calculează aceeași valoare concretă (eventual necunoscută) pentru toate execuțiile posibile ale blocului.
 * În forma SSA, numărul valorii coincide cu numele SSA sau cu constanta care desemnează valoarea concretă.
 *
 * Numerele valorilor sunt utilizate pentru rescrierea instrucțiunilor în scopul reutilizării valorilor deja calculate.
 * Strategia permite implementarea unei plaje largi de optimizări: common subexpression elimination, cu valorificarea
 * informației de comutativitate, constant folding, algebraic simplification, constant/copy propagation.
 *
 * Utilizează un tabel, în care cheile sunt nume, constante sau expresii complexe, cărora le sunt asociate numere
 * de valori, i.e. nume SSA sau constante. Cheile pentru expresii complexe se construiesc utilizând numele operatorului
 * și numerele valorilor operanzilor, pentru decuplarea de reprezentarea lor textuală și obținerea unei flexibilități
 * sporite. De exemplu, pentru expresia ADD a b, presupunând că a și b au numerele de valori x, respectiv 2,
 * cheia este ADD x 2.
 */
public class ValueNumbering {

    /**
     * Tabelul valorilor, în care păstrăm ordinea introducerii
     */
    Map<Object, Symbol> valueNumbers = new LinkedHashMap<>();

    protected void run(Instruction instruction) {
        // Instrucțiune de copiere (dinspre o altă variabilă sau o constantă)
        if (instruction.iType == InstructionType.COPY) {
            // Unicul operand
            var operand = instruction.operands.getFirst();

            // TODO Obținem din tabel numărul valorii unicului operand

            // TODO Înlocuim în instrucțiune operandul cu numărul valorii sale

            // TODO Asociem în tabel rezultatul cu numărul valorii operandului

        }
        // Operație aritmetică binară
        else if (Arrays.asList(InstructionType.ADD, InstructionType.SUB, InstructionType.MUL, InstructionType.DIV)
                .contains(instruction.iType)) {
            // Cei doi operanzi
            var leftOperand  = instruction.operands.get(0);
            var rightOperand = instruction.operands.get(1);

            // TODO Obținem din tabel numerele valorilor celor doi operanzi

            // TODO Dacă operația este comutativă, ordonăm operanzii crescător după numele lor

            // TODO Construim cheia formată din operație, numărul valorii operandului stâng, numărul valorii
            //  operandului drept. Exemplu: ADD a.0 2.
            //  Dacă expresia a fost deja evaluată, numărul valorii este cel stocat în tabel.
            //  Altfel, este rezultatul curent.

            // TODO Common subexpression elimination: numărul valorii cheii constituie o intrare anterioară din tabel
            //  (expresia a fost evaluată anterior)
            //  Completați condiția if-ului
            if (true) {
                // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi, care conține
                //  acum o singură intrare, cu rezultatul calculat anterior

            }
            // TODO Expresia nu a fost evaluată anterior: numărul valorii cheii corespunde unei intrări noi
            else {
                // TODO Constant folding: ambii operanzi sunt literali întregi
                //  Completați condiția if-ului
                if (true) {
                    // TODO Evaluăm instrucțiunea

                    // TODO Stocăm rezultatul constant în tabel

                    // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi,
                    //  care conține acum o singură intrare, cu rezultatul constant

                    // TODO Conectăm în tabel cheia la operandul constant

                }
                // TODO Algebraic simplification: 0 + x = x
                //  Completați condiția if-ului
                else if (true) {
                    // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi,
                    //  care conține acum o singură intrare, cu numărul valorii celuilalt operand

                    // TODO Conectăm cheia la numărul valorii operandului rămas

                }
            }

            // TODO Indiferent dacă expresia a fost evaluată anterior sau nu, rezultatul operației trebuie
            //  să aibă același număr de valoare cu cheia expresiei

        }

        // TODO Indiferent de tipul instrucțiunii, în cazul în care nu s-au putut realiza prelucrările specifice
        //  de mai sus, putem încerca să propagăm copii/constante pt operanzi, înlocuind operanzii cu numerele valorilor
        //  lor.

    }

    /**
     * Prelucrează un singur bloc
     *
     * @param block blocul de prelucrat
     */
    public void run(BasicBlock block) {
        // Parcurgem fiecare instrucțiune din bloc
        block.instructions.forEach(this::run);
    }

    /**
     * Determină numărul valorii, pe baza cheii. Dacă cheia există deja, intrarea corespunzătoare din tabel
     * este întoarsă. Altfel, este creată o nouă intrare pentru cheie, utilizând al doilea parametru.
     *
     * @param value       cheia de căutare din tabel
     * @param valueNumber numărul valorii
     * @return            intrarea din tabel (existentă sau nouă)
     */

    protected Symbol getValueNumber(Object value, Symbol valueNumber) {
        return valueNumbers.computeIfAbsent(value, _ -> valueNumber);
    }

    /**
     * Reprezentarea sub formă de șir a analizei conține tabelul cu 3 coloane populat pe parcurs
     *
     * @return reprezentarea ca șir
     */
    @Override
    public String toString() {
        return String.format("%-12s %s\n", "key", "value number")
                + valueNumbers.entrySet().stream()
                .map(entry -> String.format(
                        "%-12s %s",
                        entry.getKey() instanceof Symbol symbol ? symbol.getDisplayName() : entry.getKey(),
                        entry.getValue().getDisplayName()))
                .collect(Collectors.joining("\n"));
    }
}
