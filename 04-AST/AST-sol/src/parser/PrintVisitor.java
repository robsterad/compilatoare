package parser;

public class PrintVisitor implements ASTVisitor<Void> {
    int indent = 0;

    @Override
    public Void visit(Id id) {
        printIndent("ID " + id.token.getText());
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral) {
        printIndent("INT " + intLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(If iff) {
        printIndent("If");
        indent++;
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(For forr) {
        printIndent("For");
        indent++;
        forr.init.accept(this);
        forr.cond.accept(this);
        forr.step.accept(this);
        forr.body.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(FloatLiteral floaty) {
        printIndent("FLOAT " + floaty.token.getText());
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        printIndent("BOOL " + boolLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(UnaryMinus uMinus) {
        printIndent("-");
        indent++;
        uMinus.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        printIndent(arithmetic.getToken().getText());
        indent++;
        arithmetic.left.accept(this);
        arithmetic.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        printIndent("Assign");
        indent++;
        assign.id.accept(this);
        assign.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(GlobalVarDef globalVarDef) {
        printIndent("GlobalVarDef");
        indent++;
        globalVarDef.type.accept(this);
        globalVarDef.id.accept(this);
        if (globalVarDef.initValue != null)
            globalVarDef.initValue.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        printIndent("FuncDef");
        indent++;
        funcDef.type.accept(this);
        funcDef.id.accept(this);
        funcDef.formalDefs.forEach(f -> f.accept(this));
        funcDef.body.accept(this);
        indent--;

        return null;
    }

    @Override
    public Void visit(Call call) {
        printIndent("Call");
        indent++;
        call.id.accept(this);
        call.args.forEach(a -> a.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(Relational rel) {
        printIndent(rel.getToken().getText());
        indent++;
        rel.left.accept(this);
        rel.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Type type) {
        printIndent("TYPE " + type.token.getText());
        return null;
    }

    @Override
    public Void visit(FormalDef formalDef) {
        printIndent("Formal");
        indent++;
        formalDef.type.accept(this);
        formalDef.id.accept(this);
        indent--;

        return null;
    }

    @Override
    public Void visit(LocalVarDef localVarDef) {
        printIndent("LocalVarDef");
        indent++;
        localVarDef.type.accept(this);
        localVarDef.id.accept(this);
        if (localVarDef.initValue != null)
            localVarDef.initValue.accept(this);
        indent--;

        return null;
    }

    @Override
    public Void visit(Block block) {
        printIndent("Block");
        indent++;
        block.stmts.forEach(s -> s.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(Program program) {
        program.stmts.forEach(s -> s.accept(this));

        return null;
    }

    void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("\t");
        System.out.println(str);
    }
}