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

    void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("\t");
        System.out.println(str);
    }
}