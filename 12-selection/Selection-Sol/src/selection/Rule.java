package selection;

import semantic.Symbol;

import java.util.List;

/**
 * Regulă de acoperire
 */
public abstract class Rule {
    /**
     * Numele regulii
     */
    public String name;

    /**
     * Interpretarea produsă de regulă
     */
    public StorageClass LHS;

    /**
     * Costul regulii
     */
    public int cost;

    public Rule(String name, StorageClass LHS, int cost) {
        this.name = name;
        this.LHS = LHS;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "rule \"" + name + "\", cost " + cost + ": " + LHS.toString().toLowerCase() + " -> ";
    }

    /**
     * Instanța unei reguli de acoperire.
     */
    public abstract class RuleInstance {
        /**
         * Simbolul corespunzător părții stângi. Corespunde membrului LHS din clasa externă.
         */
        public Symbol LHS;

        /**
         * Costul total al instanței, dat de regula instanțiată și de costurile instanțelor copiilor
         */
        public int cost;

        public RuleInstance(Symbol LHS) {
            this.LHS = LHS;
            this.cost = Rule.this.cost;
        }

        /**
         * Întoarce lista de instrucțiuni generate doar pentru nodul curent
         *
         * @return lista de instrucțiuni
         */
        public abstract List<AssemblyInstruction> getLocalInstructions();

        /**
         * Întoarce lista de instrucțiuni generate pentru nodul curent și pentru eventualii copii
         *
         * @return lista de instrucțiuni
         */
        public abstract List<AssemblyInstruction> getGlobalInstructions();

        @Override
        public String toString() {
            return "rule \"" + name + "\" instance, cost " + cost + ": ";
        }
    }
}
