package selection;

import semantic.Symbol;

import java.util.List;

/**
 * Instrucțiune de limbaj de asamblare, reprezentată prin lista de operanzi
 */
public class AssemblyInstruction {
    public List<Symbol> operands;

    public AssemblyInstruction(Symbol... operands) {
        this.operands = List.of(operands);
    }
}
