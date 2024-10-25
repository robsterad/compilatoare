package parser;
import org.antlr.v4.runtime.*;

public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(IntLiteral intLiteral);
    T visit(If iff);
    // TODO 2: Adăugați metode pentru fiecare clasă definită anterior
}
