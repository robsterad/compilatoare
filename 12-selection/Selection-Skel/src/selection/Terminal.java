package selection;

/**
 * Tipurile posibile ale terminalilor
 */
public enum Terminal {
    /**
     * Literal întreg
     */
    LITERAL,

    /**
     * Nume extern, care nu corespunde unei instrucțiuni SSA
     */
    NAME
}
