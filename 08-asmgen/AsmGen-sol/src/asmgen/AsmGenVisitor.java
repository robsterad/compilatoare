package asmgen;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import parser.ASTNode;
import parser.ASTVisitor;

import java.util.*;

/**
 * AsmGenVisitor - Generates MIPS assembly code from an AST
 * This visitor traverses the AST and generates corresponding MIPS assembly instructions
 * Uses StringTemplate for code generation and handles:
 * - Stack frame management
 * - Variable scoping and access
 * - Type-specific operations
 * - Control flow structures
 */

public class AsmGenVisitor implements ASTVisitor<ST> {

    private static STGroupFile templates = new STGroupFile("asmgen/asmgen.stg");  // Template file for code generation
    private static int labelCounter = 0;  // Counter for generating unique labels in assembly

    // Main sections of the generated assembly
    private ST mainSection;    // Contains the main program instructions
    private ST dataSection;    // Contains global variable declarations (.data section)
    private ST funcSection;    // Contains function definitions (in .text section)

    /**
     * Returns the next unique label number and increments the counter
     * Used for generating unique labels in assembly code (e.g., for control flow)
     * @return The next available label number
     */
    private int getNextLabel() {
        return labelCounter++;
    }

    // Variable and scope management
    private final Map<String, List<Integer>> variableOffsets = new HashMap<>();  // Maps variable names to stack offsets at each depth
    private static int currentBlockDepth = 0;  // Current nesting level of blocks (0 = global scope)
    private final List<Integer> blockFrameSizes = new ArrayList<>();  // Stack frame sizes for each block level

    /**
     * ExprType - Represents different types of expressions for template selection
     * INTEGER_BOOLEAN: For int and boolean values (stored in $a0)
     * FLOAT: For floating point values (stored in $f0)
     * MIXED: For expressions involving both types (requires conversion)
     */
    private enum ExprType {
        INTEGER_BOOLEAN, FLOAT, MIXED
    }

    /**
     * TemplateSelector - Handles template selection based on expression types
     * Provides methods to determine appropriate templates for different operations
     * based on the types of operands involved
     */

    private static class TemplateSelector {
        private final STGroupFile templates;

        public TemplateSelector(STGroupFile templates) {
            this.templates = templates;
        }

        // Determines expression type based on template name
        public ExprType getExprType(ST expr) {
            String templateName = expr.getName();
            if (isFloatTemplate(templateName)) {
                return ExprType.FLOAT;
            }
            if (isIntBoolTemplate(templateName)) {
                return ExprType.INTEGER_BOOLEAN;
            }
            return ExprType.MIXED;
        }

        private boolean isFloatTemplate(String name) {
            return name.matches("/(floatLiteral|unaryMinusFloat|float_globalId|float_localId|floatBinaryOp|mixedBinaryOp)");
        }

        private boolean isIntBoolTemplate(String name) {
            return name.matches("/(literal|unaryMinus|int_bool_globalId|int_bool_localId|binaryOp)");
        }

        // Methods to get appropriate templates based on expression types
        public ST getBinaryOpTemplate(ExprType leftType, ExprType rightType) {
            if (leftType == ExprType.FLOAT && rightType == ExprType.FLOAT) {
                return templates.getInstanceOf("floatBinaryOp");
            }
            if (leftType == ExprType.INTEGER_BOOLEAN && rightType == ExprType.INTEGER_BOOLEAN) {
                return templates.getInstanceOf("binaryOp");
            }
            return templates.getInstanceOf("mixedBinaryOp");
        }

        public ST getComparisonTemplate(ExprType leftType, ExprType rightType) {
            if (leftType == ExprType.FLOAT && rightType == ExprType.FLOAT) {
                return templates.getInstanceOf("floatComparison");
            }
            if (leftType == ExprType.INTEGER_BOOLEAN && rightType == ExprType.INTEGER_BOOLEAN) {
                return templates.getInstanceOf("comparison");
            }
            return templates.getInstanceOf("mixedComparison");
        }

        public ST getParamTemplate(ExprType paramType) {
            if (paramType == ExprType.FLOAT) {
                return templates.getInstanceOf("float_arg");
            }
            return templates.getInstanceOf("int_bool_arg");
        }

        public ST getAssignTemplate(ExprType assignType) {
            if (assignType == ExprType.FLOAT) {
                return templates.getInstanceOf("float_assign");
            }
            return templates.getInstanceOf("int_bool_assign");
        }

        public ST getLocalVarDefTemplate(ExprType localType) {
            if (localType == ExprType.FLOAT) {
                return templates.getInstanceOf("float_localVarDef");
            }
            return templates.getInstanceOf("int_bool_localVarDef");
        }
    }

    private static final TemplateSelector templateSelector = new TemplateSelector(templates);

    /**
     * updateOffsetsForNewBlock - Updates variable offsets when entering a new block
     * Ensures proper variable access across nested scopes by adjusting stack offsets
     * @param frameSize Size of the new block's stack frame in bytes
     */

    private void updateOffsetsForNewBlock(int frameSize) {
        // For each variable in our offset map
        for (Map.Entry<String, List<Integer>> entry : variableOffsets.entrySet()) {
            List<Integer> offsets = entry.getValue();

            // Ensure the list has enough capacity for the current depth
            while (offsets.size() <= currentBlockDepth) {
                offsets.add(null);
            }

            // Special handling for first block (depth 0)
            if (currentBlockDepth == 0) {
                // Don't modify offsets for first block - they're set directly
                continue;
            }

            // For deeper blocks, update based on previous block's offset
            Integer previousOffset = offsets.get(currentBlockDepth - 1);
            if (previousOffset != null) {
                offsets.set(currentBlockDepth, previousOffset + frameSize);
            }
        }
    }

    /*
     * TODO 1: Plain numbers
     */

    @Override
    public ST visit(ASTNode.IntLiteral val) {
        return templates.getInstanceOf("literal")
                .add("value", val.getToken().getText());
    }

    @Override
    public ST visit(ASTNode.FloatLiteral val) {
        return templates.getInstanceOf("floatLiteral")
                .add("value", val.getToken().getText());
    }

    @Override
    public ST visit(ASTNode.BoolLiteral val) {
        return templates.getInstanceOf("boolLiteral")
                .add("value", Boolean.parseBoolean(val.getToken().getText()));
    }

    /*
     * TODO 1: Unary operations
     */

    @Override
    public ST visit(ASTNode.UnaryMinus uMinus) {
        if (uMinus.expr.accept(this).getName().equals("/floatLiteral")) {
            return templates.getInstanceOf("unaryMinusFloat")
                    .add("e", uMinus.expr.accept(this))
                    .add("dStr", uMinus.debugStr);
        }
        return templates.getInstanceOf("unaryMinus")
                .add("e", uMinus.expr.accept(this))
                .add("dStr", uMinus.debugStr);
    }

    /*
     * TODO 2: Binary operations
     */

    @Override
    public ST visit(ASTNode.Arithmetic expr) {
        ST leftExpr = expr.left.accept(this);
        ST rightExpr = expr.right.accept(this);

        if (leftExpr == null || rightExpr == null) {
            return null;
        }

        String op = getArithmeticOperator(expr.getToken().getText());
        ExprType leftType = templateSelector.getExprType(leftExpr);
        ExprType rightType = templateSelector.getExprType(rightExpr);

        ST template = templateSelector.getBinaryOpTemplate(leftType, rightType);

        return template.add("e1", leftExpr)
                .add("e2", rightExpr)
                .add("op", op)
                .add("leftIsInt", leftType == ExprType.INTEGER_BOOLEAN)
                .add("dStr", expr.debugStr);
    }

    private String getArithmeticOperator(String token) {
        return switch (token) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "div";
            default -> throw new RuntimeException("Unknown operator: " + token);
        };
    }

    @Override
    public ST visit(ASTNode.Relational expr) {
        ST leftExpr = expr.left.accept(this);
        ST rightExpr = expr.right.accept(this);

        String op = getRelationalOperator(expr.getToken().getText());
        ExprType leftType = templateSelector.getExprType(leftExpr);
        ExprType rightType = templateSelector.getExprType(rightExpr);

        ST template = templateSelector.getComparisonTemplate(leftType, rightType);
        return template.add("e1", leftExpr)
                .add("e2", rightExpr)
                .add("op", op)
                .add("leftIsInt", leftType == ExprType.INTEGER_BOOLEAN)
                .add("label", getNextLabel())
                .add("dStr", expr.debugStr);
    }

    private String getRelationalOperator(String token) {
        return switch (token) {
            case "<" -> "lt";
            case "<=" -> "le";
            case "==" -> "eq";
            default -> throw new RuntimeException("Unknown operator: " + token);
        };
    }

    /*
     * TODO 3: Control structures
     */

    @Override
    public ST visit(ASTNode.If iff) {
        int label = getNextLabel();
        return templates.getInstanceOf("if")
                .add("cond", iff.cond.accept(this))
                .add("thenBranch", iff.thenBranch.accept(this))
                .add("elseBranch", iff.elseBranch != null ? iff.elseBranch.accept(this) : "")
                .add("label", label).add("dStr", iff.debugStr);
    }

    @Override
    public ST visit(ASTNode.Call call) {
        ST st = templates.getInstanceOf("functionCall");

        ST params = templates.getInstanceOf("sequence");
        Iterator<ASTNode.Expression> param_iter = call.args.descendingIterator();

        while (param_iter.hasNext()) {
            ASTNode.Expression param = param_iter.next();

            ExprType type = templateSelector.getExprType(param.accept(this));
            ST param_st = templateSelector.getParamTemplate(type);

            param_st.add("expr", param.accept(this));
            params.add("e", param_st);
        }

        st.add("name", call.id.getToken().getText());
        st.add("args", params);
        st.add("dStr", call.debugStr);

        return st;
    }

    /*
     * TODO 4: Variable definitions, assignments and blocks
     */

    @Override
    public ST visit(ASTNode.GlobalVarDef varDef) {

        // Add variable to data section
        dataSection.add("e", templates.getInstanceOf("globalVarDef")
                .add("name", varDef.id.getToken().getText())
                .add("is_float", varDef.type.getToken().getText().equals("Float")));

        // If there's an initialization expression, add it to main
        if (varDef.initValue != null) {
            ExprType type = templateSelector.getExprType(varDef.initValue.accept(this));
            return templateSelector.getAssignTemplate(type)
                    .add("expr", varDef.initValue.accept(this))
                    .add("location", varDef.id.getToken().getText())
                    .add("dStr", varDef.debugStr);
        }

        return null;
    }

    /**
     * visit(ASTNode.LocalVarDef) - Handles local variable definitions
     * - Allocates space on stack
     * - Updates variable offset map
     * - Generates initialization code if needed
     */
    public ST visit(ASTNode.LocalVarDef localVarDef) {
        // If there's an initialization value, generate code for it
        if (localVarDef.initValue != null) {
            ExprType type = templateSelector.getExprType(localVarDef.initValue.accept(this));
            return templateSelector.getLocalVarDefTemplate(type)
                    .add("expr", localVarDef.initValue.accept(this))
                    .add("offset", variableOffsets.get(localVarDef.id.getSymbol().getName()).get(currentBlockDepth))
                    .add("dStr", localVarDef.debugStr);
        }
        return null;
    }

    @Override
    public ST visit(ASTNode.Assign assign) {
        String location;
        if (assign.id.getSymbol().isGlobal) {
            location = assign.id.getToken().getText();
        } else {
            location = variableOffsets.get(assign.id.getSymbol().getName()).get(currentBlockDepth) + "($fp)";
        }

        ExprType type = templateSelector.getExprType(assign.id.accept(this));

        return templateSelector.getAssignTemplate(type)
                .add("expr", assign.expr.accept(this))
                .add("location", location)
                .add("dStr", assign.debugStr);
    }


    /*
     * TODO 4: Blocks
     */

    /**
     * visit(ASTNode.Block) - Handles block-level scope
     * - Calculates frame size based on local variables
     * - Sets up stack frame and saves registers
     * - Updates variable offsets for proper scoping
     * - Processes block statements
     * - Restores stack frame on exit
     */

    @Override
    public ST visit(ASTNode.Block block) {
        // Calculate frame size first
        int blockVarsCount = 0;
        for (ASTNode stmt : block.stmts) {
            if (stmt instanceof ASTNode.LocalVarDef) {
                blockVarsCount++;
            }
        }
        int frameSize = 8 + (blockVarsCount * 4);

        // Increment depth after calculating frame size
        currentBlockDepth++;
        blockFrameSizes.add(frameSize);

        // Update offsets for outer variables
        updateOffsetsForNewBlock(frameSize);

        // Create block template and setup
        ST blockST = templates.getInstanceOf("sequence");
        ST frameSetup = templates.getInstanceOf("block_setup")
                .add("frame_size", frameSize)
                .add("return_offset", frameSize)
                .add("frame_offset", frameSize - 4);
        blockST.add("e", frameSetup);

        // Process statements
        int localVarOffset = 0;
        for (ASTNode stmt : block.stmts) {
            if (stmt instanceof ASTNode.LocalVarDef localVarDef) {
                // Calculate offset for new local variable
                int offset = 4 + (localVarOffset * 4);
                localVarDef.id.getSymbol().setOffset(offset);

                // Update offset map
                String varName = localVarDef.id.getToken().getText();
                List<Integer> offsets = variableOffsets.computeIfAbsent(varName, k -> new ArrayList<>());
                while (offsets.size() <= currentBlockDepth) {
                    offsets.add(null);
                }
                offsets.set(currentBlockDepth, offset);

                localVarOffset++;
            }

            ST stmtST = stmt.accept(this);
            if (stmtST != null) {
                blockST.add("e", stmtST);
            }
        }

        // Frame cleanup
        ST frameCleanup = templates.getInstanceOf("block_cleanup")
                .add("frame_size", frameSize)
                .add("return_offset", frameSize)
                .add("frame_offset", frameSize - 4);
        blockST.add("e", frameCleanup);

        // Clean up before exiting block
        currentBlockDepth--;
        blockFrameSizes.remove(blockFrameSizes.size() - 1);

        return blockST;
    }

    /*
     * TODO 5: Function definitions
     */

    /**
     * visit(ASTNode.FuncDef) - Handles function definitions
     * - Sets up parameter offsets
     * - Generates function prologue
     * - Processes function body
     * - Generates function epilogue
     */
    @Override
    public ST visit(ASTNode.FuncDef funcDef) {
        // Handle parameters first
        int paramOffset = 8;  // Initial offset for parameters
        for (ASTNode.FormalDef formal : funcDef.formalDefs) {
            String paramName = formal.id.getToken().getText();
            List<Integer> offsets = new ArrayList<>();
            offsets.add(paramOffset + funcDef.body.localVarDefNum * 4 + 12);
            variableOffsets.put(paramName, offsets);
            paramOffset += 4;
        }

        // Process function body
        ST st = templates.getInstanceOf("functionDef").
                add("name", funcDef.id.getToken().getText()).
                add("body", funcDef.body.accept(this)).
                add("local_offset", funcDef.formalDefs.size() * 4 + 8)
                .add("dStr", funcDef.debugStr);

        funcSection.add("e", st);
        return null;
    }

    @Override
    public ST visit(ASTNode.FormalDef formal) {
        // TODO 5
        return null;
    }

    /*
     * TODO 5: Variable references
     */

    /**
     * visit(ASTNode.Id) - Handles variable references
     * - Determines if variable is global or local
     * - Generates appropriate load instruction based on type
     * - Uses correct offset for local variables
     */
    @Override
    public ST visit(ASTNode.Id id) {
        if (id.getSymbol().isGlobal) {
            // Global variable handling remains the same
            if (id.getSymbol().getType().getName().equals("Float"))
                return templates.getInstanceOf("float_globalId").add("address", id.getToken().getText());
            return templates.getInstanceOf("int_bool_globalId").add("address", id.getToken().getText());
        }

        // For local variables, we need to determine if they're in the current
        // frame or an outer frame
        int offset = variableOffsets.get(id.getSymbol().getName()).get(currentBlockDepth);
        if (id.getSymbol().getType().getName().equals("Float")) {
            return templates.getInstanceOf("float_localId")
                    .add("offset", offset);
        }
        return templates.getInstanceOf("int_bool_localId")
                .add("offset", offset);
    }

    /*
     * TODO 6: For
     */

    @Override
    public ST visit(ASTNode.For aFor) {
        int label = getNextLabel();
        return templates.getInstanceOf("forLoop")
                .add("init", aFor.init.accept(this))
                .add("cond", aFor.cond.accept(this))
                .add("step", aFor.step.accept(this))
                .add("body", aFor.body.accept(this))
                .add("label", label).add("dStr", aFor.debugStr);
    }

    @Override
    public ST visit(ASTNode.Type type) {
        return null;
    }

    @Override
    public ST visit(ASTNode.Program program) {
        dataSection = templates.getInstanceOf("sequenceSpaced");
        funcSection = templates.getInstanceOf("sequenceSpaced");
        mainSection = templates.getInstanceOf("sequence");

        for (ASTNode e : program.stmts) {
            if (e instanceof ASTNode.FuncDef || e instanceof ASTNode.Block) {
                // Reset state for new function or new block
                currentBlockDepth = -1;  // Will become 0 when entering first block
                variableOffsets.clear();
                blockFrameSizes.clear();
            }
            mainSection.add("e", e.accept(this));
        }

        //assembly-ing it all together. HA! get it?
        var programST = templates.getInstanceOf("program");
        programST.add("data", dataSection);
        programST.add("textFuncs", funcSection);
        programST.add("textMain", mainSection);

        return programST;
    }

}
