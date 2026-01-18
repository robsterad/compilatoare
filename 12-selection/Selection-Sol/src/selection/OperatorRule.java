package selection;

import irgen.InstructionType;
import semantic.Symbol;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Regulă cu operator
 */
public class OperatorRule extends Rule {
    /**
     * Operatorul regulii
     */
    public InstructionType operator;

    /**
     * Interpretările copiilor
     */
    public List<StorageClass> RHS;

    /**
     * Funcție care produce lista de instrucțiuni pe baza unei instanțe a regulii. În absența unei instanțe nu am ști
     * cu ce să umplem operanzii instrucțiunilor.
     */
    public Function<OperatorRuleInstance, List<AssemblyInstruction>> instructionRecipe;

    public OperatorRule(
            String name,
            StorageClass LHS,
            InstructionType operator,
            List<StorageClass> RHS,
            Function<OperatorRuleInstance, List<AssemblyInstruction>> instructionRecipe,
            int cost
    ) {
        super(name, LHS, cost);
        this.operator = operator;
        this.RHS = RHS;
        this.instructionRecipe = instructionRecipe;
    }

    @Override
    public String toString() {
        return "operator " + super.toString()
                + operator + " "
                + RHS.stream()
                        .map(storageClass -> storageClass.toString().toLowerCase())
                        .collect(Collectors.joining(" "));
    }

    /**
     * Instanță a unei reguli cu operator
     */
    public class OperatorRuleInstance extends RuleInstance {
        /**
         * Lista de instanțe de reguli aferente copiilor. Corespunde membrului RHS ai clasei externe.
         */
        public List<RuleInstance> RHS;

        public OperatorRuleInstance(Symbol LHS, List<RuleInstance> RHS) {
            super(LHS);
            this.RHS = RHS;

            // Adăugăm costul instanțelor copiilor la costul regulii curente
            this.cost += RHS.stream()
                    .mapToInt(instance -> instance.cost)
                    .sum();
        }

        /**
         * Întoarce lista de instrucțiuni generate doar pentru nodul curent
         *
         * @return lista de instrucțiuni
         */
        @Override
        public List<AssemblyInstruction> getLocalInstructions() {
            // Aplicăm funcția din reprezentarea regulii externe pe instanța curentă
            return instructionRecipe.apply(this);
        }

        /**
         * Întoarce lista de instrucțiuni generate pentru nodul curent și pentru eventualii copii
         *
         * @return lista de instrucțiuni
         */
        @Override
        public List<AssemblyInstruction> getGlobalInstructions() {
            // Generăm mai întâi instrucțiunile aferente copiilor
            var instructions = RHS.stream()
                    .flatMap(instance -> instance.getGlobalInstructions().stream())
                    .collect(Collectors.toList());

            // Apoi adăugăm instrucțiunile aferente regulii curente
            instructions.addAll(getLocalInstructions());

            return instructions;
        }

        /**
         * Întoarce operandul cu numărul de ordine furnizat din cadrul grupului de operanzi cu o anumită interpretare
         *
         * @param storageClass interpretarea
         * @param index        numărul de ordine din cadrul acelei interpretări
         * @return             operandul
         * @throws IllegalArgumentException dacă nu există un operand cu interpretarea și numărul de ordine precizate
         */
        public RuleInstance getOperandByStorageClass(StorageClass storageClass, int index) {
            var counter = -1;

            for (var i = 0; i < OperatorRule.this.RHS.size(); i++) {
                if (OperatorRule.this.RHS.get(i) == storageClass)
                    counter++;

                if (counter == index)
                    return RHS.get(i);
            }

            throw new IllegalArgumentException("No " + storageClass + " argument with index " + index);
        }

        @Override
        public String toString() {
            var result = "operator " + super.toString()
                    + OperatorRule.this.LHS.toString().toLowerCase() + "(" + LHS.getDisplayName() + ")"
                    + " -> "
                    + operator;

            for (var i = 0; i < RHS.size(); i++)
                result += " " + OperatorRule.this.RHS.get(i).toString().toLowerCase()
                        + "(" + RHS.get(i).LHS.getDisplayName() + ")";

            result += " => " + "local: " + getLocalInstructions() + " / global: " + getGlobalInstructions();

            return result;
        }
    }
}
