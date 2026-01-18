import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import irgen.Instruction;
import irgen.InstructionType;
import selection.*;
import semantic.*;

public class Test {

    public static void main(String[] args) throws IOException {
        // TERMINAL RULES

        var immediateLiteralRule = new TerminalRule(
                "1",
                StorageClass.IMMEDIATE,
                Terminal.LITERAL,
                symbol -> symbol,
                instance -> List.of(),
                0
        );

        var registerLiteralRule = new TerminalRule(
                "2",
                StorageClass.REGISTER,
                Terminal.LITERAL,
                symbol -> new IdSymbol("regLit").applyNextVersion(),
                instance -> List.of(new MIPSInstruction(MIPSInstructionType.LI, instance.LHS, instance.RHS)),
                1
        );

        var registerNameRule = new TerminalRule(
                "3",
                StorageClass.REGISTER,
                Terminal.NAME,
                symbol -> symbol,
                instance -> List.of(),
                0
        );

        // OPERATOR RULES

        var registerAddRegistersRule = new OperatorRule(
                "4",
                StorageClass.REGISTER,
                InstructionType.ADD,
                List.of(StorageClass.REGISTER, StorageClass.REGISTER),
                instance -> List.of(new MIPSInstruction(
                        MIPSInstructionType.ADD,
                        instance.LHS,
                        instance.getOperandByStorageClass(StorageClass.REGISTER, 0).LHS,
                        instance.getOperandByStorageClass(StorageClass.REGISTER, 1).LHS
                )),
                1
        );

        var registerAddRegisterImmediateRule = new OperatorRule(
                "5",
                StorageClass.REGISTER,
                InstructionType.ADD,
                List.of(StorageClass.REGISTER, StorageClass.IMMEDIATE),
                instance -> List.of(new MIPSInstruction(
                        MIPSInstructionType.ADDI,
                        instance.LHS,
                        instance.getOperandByStorageClass(StorageClass.REGISTER,  0).LHS,
                        instance.getOperandByStorageClass(StorageClass.IMMEDIATE, 0).LHS
                )),
                1
        );

        // Operanzii sunt în ordine inversă față de regula anterioară, dar instrucțiunea addi trebuie să primească
        // operanzii tot în ordinea registru-imediat, ca mai sus
        var registerAddImmediateRegisterRule = new OperatorRule(
                "6",
                StorageClass.REGISTER,
                InstructionType.ADD,
                List.of(StorageClass.IMMEDIATE, StorageClass.REGISTER),
                instance -> List.of(new MIPSInstruction(
                        MIPSInstructionType.ADDI,
                        instance.LHS,
                        instance.getOperandByStorageClass(StorageClass.REGISTER,  0).LHS,
                        instance.getOperandByStorageClass(StorageClass.IMMEDIATE, 0).LHS
                )),
                1
        );

        var addressAddRegisterImmediateRule = new OperatorRule(
                "7",
                StorageClass.ADDRESS,
                InstructionType.ADD,
                List.of(StorageClass.REGISTER, StorageClass.IMMEDIATE),
                instance -> List.of(),
                0
        );

        var addressAddImmediateRegisterRule = new OperatorRule(
                "8",
                StorageClass.ADDRESS,
                InstructionType.ADD,
                List.of(StorageClass.IMMEDIATE, StorageClass.REGISTER),
                instance -> List.of(),
                0
        );

        var registerLoadRegisterRule = new OperatorRule(
                "9",
                StorageClass.REGISTER,
                InstructionType.LOAD,
                List.of(StorageClass.REGISTER),
                instance -> List.of(new MIPSInstruction(
                        MIPSInstructionType.LW,
                        instance.LHS,
                        IntSymbol.get(0),
                        instance.getOperandByStorageClass(StorageClass.REGISTER, 0).LHS
                )),
                2
        );

        var registerLoadAddressRule = new OperatorRule(
                "10",
                StorageClass.REGISTER,
                InstructionType.LOAD,
                List.of(StorageClass.ADDRESS),
                instance -> {
                    var addressInstance = (OperatorRule.OperatorRuleInstance)
                            instance.getOperandByStorageClass(StorageClass.ADDRESS, 0);

                    return List.of(new MIPSInstruction(
                            MIPSInstructionType.LW,
                            instance.LHS,
                            addressInstance.getOperandByStorageClass(StorageClass.IMMEDIATE, 0).LHS,
                            addressInstance.getOperandByStorageClass(StorageClass.REGISTER, 0).LHS
                    ));
                },
                2
        );

        var terminalRules = Set.of(
                immediateLiteralRule,
                registerLiteralRule,
                registerNameRule
        );

        var operatorRules = Set.of(
                registerAddRegistersRule,
                registerAddRegisterImmediateRule,
                registerAddImmediateRegisterRule,
                addressAddRegisterImmediateRule,
                addressAddImmediateRegisterRule,
                registerLoadRegisterRule,
                registerLoadAddressRule
        );

        System.out.println("TERMINAL RULES\n" + terminalRules.stream()
                .sorted(Comparator.comparingInt(rule -> Integer.parseInt(rule.name)))
                .map(Object::toString)
                .collect(Collectors.joining("\n")));

        System.out.println("\nOPERATOR RULES\n" + operatorRules.stream()
                .sorted(Comparator.comparing(rule -> Integer.parseInt(rule.name)))
                .map(Object::toString)
                .collect(Collectors.joining("\n")));

        // Exemplul din descrierea laboratorului
        var x = new IdSymbol("x");
        var y = new IdSymbol("y");

        var a = new Instruction(InstructionType.ADD, "a", x, IntSymbol.get(4));
        var b = new Instruction(InstructionType.LOAD, "b", a);
        var c = new Instruction(InstructionType.ADD, "c", b, y);

        System.out.println("\n" + Stream.of(a, b, c)
                .map(Object::toString)
                .collect(Collectors.joining("\n")));

        // Tiler
        var tiler = new Tiler(terminalRules, operatorRules);
        tiler.tile(c);

        System.out.println("\nTILING:\n" + tiler);
    }

}
