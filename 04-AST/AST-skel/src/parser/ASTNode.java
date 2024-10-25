package parser;
import org.antlr.v4.runtime.Token;
import java.util.*;

// Rădăcina ierarhiei de clase reprezentând nodurile arborelui de sintaxă
// abstractă (AST). Singura metodă permite primirea unui visitor.
public abstract class ASTNode {
    // Reținem un token descriptiv al nodului, pentru a putea afișa ulterior
    // informații legate de linia și coloana eventualelor erori semantice.
    protected Token token;
    public String debugStr = null;		// used in codegen

    ASTNode(Token token) {
        this.token = token;
    }

    Token getToken() {
        return token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

// Orice expresie.
abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}

// Identificatori
class Id extends Expression {
    Id(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Literali întregi
class IntLiteral extends Expression {
    IntLiteral(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Construcția if.
class If extends Expression {
    // Sunt necesare trei câmpuri pentru cele trei componente ale expresiei.
    Expression cond;
    Expression thenBranch;
    Expression elseBranch;

    If(Expression cond,
       Expression thenBranch,
       Expression elseBranch,
       Token start) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// TODO 1: Definiți restul claselor de care ați avea nevoie. Asigurați-vă
// că moșteniți mereu nodul de bază ASTNode