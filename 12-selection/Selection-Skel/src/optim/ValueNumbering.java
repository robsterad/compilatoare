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

    /**
     * Analizează și eventual rescrie o instrucțiune, adăugând intrări noi în tabelul valorilor.
     *
     * @param instruction instrucțiunea curentă
     */
    protected void run(Instruction instruction) {
        // Instrucțiune de copiere (dinspre o altă variabilă sau o constantă)
        if (instruction.iType == InstructionType.COPY) {
            // Unicul operand
            var operand = instruction.operands.getFirst();

            // TODO Obținem din tabel numărul valorii unicului operand
            var operandValueNumber = getValueNumber(operand, operand);

            // TODO Înlocuim în instrucțiune operandul cu numărul valorii sale
            instruction.operands = Collections.singletonList(operandValueNumber);

            // TODO Asociem în tabel rezultatul cu numărul valorii operandului
            valueNumbers.put(instruction, operandValueNumber);
        }
        // Operație aritmetică binară
        else if (Arrays.asList(InstructionType.ADD, InstructionType.SUB, InstructionType.MUL, InstructionType.DIV,
                        InstructionType.CMP_EQ, InstructionType.CMP_LT, InstructionType.CMP_LE)
                .contains(instruction.iType)) {
            // Cei doi operanzi
            var leftOperand  = instruction.operands.get(0);
            var rightOperand = instruction.operands.get(1);

            // TODO Obținem din tabel numerele valorilor celor doi operanzi
            var leftValueNumber  = getValueNumber(leftOperand,  leftOperand);
            var rightValueNumber = getValueNumber(rightOperand, rightOperand);

            // TODO Dacă operația este comutativă, ordonăm operanzii crescător după numele lor
            var operands = new Symbol[] { leftValueNumber, rightValueNumber };
            if (instruction.isCommutative())
                Arrays.sort(operands, Comparator.comparing(Symbol::getName));

            // TODO Construim cheia formată din operație, numărul valorii operandului stâng, numărul valorii
            //  operandului drept. Exemplu: ADD a.0 2.
            //  Dacă expresia a fost deja evaluată, numărul valorii este cel stocat în tabel.
            //  Altfel, este rezultatul curent.
            var key = instruction.iType + " " + operands[0].getDisplayName() + " " + operands[1].getDisplayName();

            // TODO Determinăm numărul valorii cheii
            var keyValueNumber = getValueNumber(key, instruction);

            // TODO Common subexpression elimination: numărul valorii cheii constituie o intrare anterioară din tabel
            //  (expresia a fost evaluată anterior)
            //  Completați condiția if-ului
            if (keyValueNumber != instruction) {
                // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi, care conține
                //  acum o singură intrare, cu rezultatul calculat anterior
                instruction.iType = InstructionType.COPY;
                instruction.operands = Collections.singletonList(keyValueNumber);
            }
            // TODO Expresia nu a fost evaluată anterior: numărul valorii cheii corespunde unei intrări noi
            else {
                // TODO Constant folding: ambii operanzi sunt literali întregi
                //  Completați condiția if-ului
                if (leftValueNumber instanceof IntSymbol left && rightValueNumber instanceof IntSymbol right) {
                    // TODO Evaluăm instrucțiunea
                    var resultValue = switch (instruction.iType) {
                        case ADD -> left.value + right.value;
                        case SUB -> left.value - right.value;
                        case MUL -> left.value * right.value;
                        case DIV -> left.value / right.value;
                        case CMP_EQ -> left.value == right.value ? 1 : 0;
                        case CMP_LT -> left.value <  right.value ? 1 : 0;
                        case CMP_LE -> left.value <= right.value ? 1 : 0;
                        default -> throw new UnsupportedOperationException();
                    };

                    // TODO Stocăm rezultatul constant în tabel
                    var result = IntSymbol.get(resultValue);
                    var resultValueNumber = getValueNumber(result, result);

                    // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi,
                    //  care conține acum o singură intrare, cu rezultatul constant
                    instruction.iType = InstructionType.COPY;
                    instruction.operands = Collections.singletonList(resultValueNumber);

                    // TODO Conectăm în tabel cheia la operandul constant
                    valueNumbers.put(key, resultValueNumber);
                }
                // TODO Algebraic simplification: 0 + x = x
                //  Completați condiția if-ului
                else if (instruction.iType == InstructionType.ADD
                        && leftValueNumber instanceof IntSymbol literal
                        && literal.value == 0) {
                    // TODO Schimbăm tipul instrucțiunii ca fiind de copiere și refacem lista de operanzi,
                    //  care conține acum o singură intrare, cu numărul valorii celuilalt operand
                    instruction.iType = InstructionType.COPY;
                    instruction.operands = Collections.singletonList(rightValueNumber);

                    // TODO Conectăm cheia la numărul valorii operandului rămas
                    valueNumbers.put(key, rightValueNumber);
                }
            }

            // TODO Indiferent dacă expresia a fost evaluată anterior sau nu, rezultatul operației trebuie să aibă
            //  același număr de valoare cu cheia expresiei
            valueNumbers.put(instruction, getValueNumber(key, null));
        }

        // TODO Indiferent de tipul instrucțiunii, în cazul în care nu s-au putut realiza prelucrările specifice
        //  de mai sus, putem încerca să propagăm copii/constante pt operanzi, înlocuind operanzii cu numerele valorilor
        //  lor.
        instruction.operands = instruction.operands.stream()
                .map(operand -> operand instanceof BasicBlock ? operand : getValueNumber(operand, operand))
                .collect(Collectors.toList());
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
