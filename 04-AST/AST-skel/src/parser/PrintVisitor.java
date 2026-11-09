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

    // TODO 4: Afisati fiecare nod astfel incat nivelul pe care acesta
    // se afla in AST sa fie reprezentat de numarul de tab-uri.
    // Folositi functia de mai jos 'printIndent' si incrementati /
    // decrementati corespunzator numarul de tab-uri

    // Rădăcina nu se afișează; definițiile și expresiile de la nivelul
    // programului apar fără indentare.
    @Override
    public Void visit(Program program) {
        for (var stmt : program.stmts) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(FloatLiteral floatLiteral) {
        printIndent("FLOAT " + floatLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        printIndent("BOOL " + boolLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(UnaryMinus unaryMinus) {
        printIndent("UnaryMinus");
        indent++;
        unaryMinus.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        printIndent(arithmetic.token.getText());
        indent++;
        arithmetic.left.accept(this);
        arithmetic.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        printIndent(relational.token.getText());
        indent++;
        relational.left.accept(this);
        relational.right.accept(this);
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
    public Void visit(Call call) {
        printIndent("Call");
        indent++;
        call.id.accept(this);
        for (var arg : call.args) {
            arg.accept(this);
        }
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
    public Void visit(Block block) {
        printIndent("Block");
        indent++;
        for (var stmt : block.stmts) {
            stmt.accept(this);
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(GlobalVarDef globalVarDef) {
        printVarDef("GlobalVarDef", globalVarDef);
        return null;
    }

    @Override
    public Void visit(LocalVarDef localVarDef) {
        printVarDef("LocalVarDef", localVarDef);
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndent("Formal");
        indent++;
        printIndent("TYPE " + formal.type.getText());
        formal.id.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        printIndent("FuncDef");
        indent++;
        printIndent("TYPE " + funcDef.type.getText());
        funcDef.id.accept(this);
        for (var formal : funcDef.formals) {
            formal.accept(this);
        }
        funcDef.body.accept(this);
        indent--;
        return null;
    }

    void printVarDef(String name, VarDef varDef) {
        printIndent(name);
        indent++;
        printIndent("TYPE " + varDef.type.getText());
        varDef.id.accept(this);
        if (varDef.init != null) {
            varDef.init.accept(this);
        }
        indent--;
    }

    void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("\t");
        System.out.println(str);
    }
}