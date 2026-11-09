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

// Programul: secvența de definiții și expresii de la nivelul cel mai înalt.
class Program extends ASTNode {
    LinkedList<ASTNode> stmts;

    Program(LinkedList<ASTNode> stmts, Token start) {
        super(start);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Literali reali
class FloatLiteral extends Expression {
    FloatLiteral(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Literali booleeni
class BoolLiteral extends Expression {
    BoolLiteral(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Minus unar: -expr
class UnaryMinus extends Expression {
    Expression expr;

    UnaryMinus(Expression expr, Token start) {
        super(start);
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Operații aritmetice binare: +, -, *, /. Token-ul este chiar operatorul.
class Arithmetic extends Expression {
    Expression left;
    Expression right;

    Arithmetic(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Operații relaționale: <, <=, ==. Token-ul este chiar operatorul.
class Relational extends Expression {
    Expression left;
    Expression right;

    Relational(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Atribuire: id = expr
class Assign extends Expression {
    Id id;
    Expression expr;

    Assign(Id id, Expression expr, Token start) {
        super(start);
        this.id = id;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Apel de funcție: id(arg1, arg2, ...)
class Call extends Expression {
    Id id;
    LinkedList<Expression> args;

    Call(Id id, LinkedList<Expression> args, Token start) {
        super(start);
        this.id = id;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Construcția for: for init, cond, step do body
class For extends Expression {
    Expression init;
    Expression cond;
    Expression step;
    Expression body;

    For(Expression init,
        Expression cond,
        Expression step,
        Expression body,
        Token start) {
        super(start);
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Bloc: secvență de definiții locale și expresii, între acolade.
// Este tot o expresie, valoarea sa fiind cea a ultimei expresii.
class Block extends Expression {
    LinkedList<ASTNode> stmts;

    Block(LinkedList<ASTNode> stmts, Token start) {
        super(start);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Definiție de variabilă: type id (= init)?
// Inițializarea poate lipsi, caz în care init este null.
abstract class VarDef extends ASTNode {
    Token type;
    Id id;
    Expression init;

    VarDef(Token type, Id id, Expression init, Token start) {
        super(start);
        this.type = type;
        this.id = id;
        this.init = init;
    }
}

// Variabilă definită la nivelul programului
class GlobalVarDef extends VarDef {
    GlobalVarDef(Token type, Id id, Expression init, Token start) {
        super(type, id, init, start);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Variabilă definită în interiorul unui bloc
class LocalVarDef extends VarDef {
    LocalVarDef(Token type, Id id, Expression init, Token start) {
        super(type, id, init, start);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Parametru formal al unei funcții: type id
class Formal extends ASTNode {
    Token type;
    Id id;

    Formal(Token type, Id id, Token start) {
        super(start);
        this.type = type;
        this.id = id;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Definiție de funcție: type id(formal1, formal2, ...) body
class FuncDef extends ASTNode {
    Token type;
    Id id;
    LinkedList<Formal> formals;
    Block body;

    FuncDef(Token type,
            Id id,
            LinkedList<Formal> formals,
            Block body,
            Token start) {
        super(start);
        this.type = type;
        this.id = id;
        this.formals = formals;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}