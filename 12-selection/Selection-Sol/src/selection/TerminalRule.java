package selection;

import semantic.Symbol;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Regulă terminală
 */
public class TerminalRule extends Rule {
    /**
     * Singurul element al părții drepte este un terminal
     */
    public Terminal RHS;

    /**
     * Funcție care produce simbolul părții stângi, pe baza simbolului părții drepte
     */
    public UnaryOperator<Symbol> getInstanceLHS;

    /**
     * Funcție care produce lista de instrucțiuni pe baza unei instanțe a regulii. În absența unei instanțe nu am ști
     * cu ce să umplem operanzii instrucțiunilor.
     */
    public Function<TerminalRuleInstance, List<AssemblyInstruction>> instructionRecipe;

    public TerminalRule(
            String name,
            StorageClass LHS,
            Terminal RHS,
            UnaryOperator<Symbol> getInstanceLHS,
            Function<TerminalRuleInstance, List<AssemblyInstruction>> instructionRecipe,
            int cost
    ) {
        super(name, LHS, cost);
        this.RHS = RHS;
        this.getInstanceLHS = getInstanceLHS;
        this.instructionRecipe = instructionRecipe;
    }

    @Override
    public String toString() {
        return "terminal " + super.toString() + RHS;
    }

    /**
     * Instanță a unei reguli terminale
     */
    public class TerminalRuleInstance extends RuleInstance {
        /**
         * Singurul element al părții drepte este un simbol. Corespunde membrului RHS din clasa externă.
         */
        public Symbol RHS;

        public TerminalRuleInstance(Symbol LHS, Symbol RHS) {
            super(LHS);
            this.RHS = RHS;
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
         * Întoarce lista de instrucțiuni generate pentru nodul curent
         *
         * @return lista de instrucțiuni
         */
        @Override
        public List<AssemblyInstruction> getGlobalInstructions() {
            // Pentru o regulă terminală, nu există ale instrucțiuni în afara celor locale
            return getLocalInstructions();
        }

        @Override
        public String toString() {
            return "terminal " + super.toString()
                    + TerminalRule.this.LHS.toString().toLowerCase() + "(" + LHS.getDisplayName() + ")"
                    + " -> "
                    + TerminalRule.this.RHS + "(" + RHS.getDisplayName() + ")"
                    + " => "
                    + getLocalInstructions();
        }
    }
}
