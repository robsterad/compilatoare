package selection;

import semantic.Symbol;

import java.util.stream.Collectors;

/**
 * Instrucțiune MIPS, reprezentată prin tipul său și lista de operanzi
 */
public class MIPSInstruction extends AssemblyInstruction {
    public MIPSInstructionType iType;

    public MIPSInstruction(MIPSInstructionType iType, Symbol... operands) {
        super(operands);
        this.iType = iType;
    }

    @Override
    public String toString() {
        var result = iType.toString().toLowerCase() + " ";

        // Afișare preferențială a instrucțiunii „load word”
        if (iType == MIPSInstructionType.LW)
            result += operands.get(0).getDisplayName() + " "
                    + operands.get(1).getDisplayName() + "("
                    + operands.get(2).getDisplayName() + ")";
        else
            result += operands.stream()
                    .map(Symbol::getDisplayName)
                    .collect(Collectors.joining(" "));

        return result;
    }
}
