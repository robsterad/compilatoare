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

// Construcția for.
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

class FloatLiteral extends Expression {
    FloatLiteral(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class BoolLiteral extends Expression {
    BoolLiteral(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Token-ul pentru un Assign va fi '='
class Assign extends Expression {
    Id id;
    Expression expr;

    Assign(Id id, Expression expr, Token token) {
        super(token);
        this.id = id;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Pentru un Relational avem 'op=(LT | LE | EQUAL)' ca reprezentare
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

// Pentru UnaryMinus, operatorul va fi '-'
class UnaryMinus extends Expression {
    Expression expr;

    UnaryMinus(Expression expr, Token op) {
        super(op);
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Apelul unei functii poate avea oricate argumente. Le vom salva intr-o lista
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

abstract class Definition extends ASTNode {
    Definition(Token token) {
        super(token);
    }
}

class Type extends ASTNode {
    Type(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Parametrul formal din definita unei functii
class FormalDef extends Definition {
    Type type;
    Id id;

    FormalDef(Type type, Id id, Token token) {
        super(token);
        this.type = type;
        this.id = id;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Definiție de variabilă locală
class LocalVarDef extends Definition {
    Type type;
    Id id;
    Expression initValue;

    LocalVarDef(Type type, Id id, Expression initValue, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.initValue = initValue;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Initializarea poate sa lipseasca
class GlobalVarDef extends Definition {
    Type type;
    Id id;
    Expression initValue;

    GlobalVarDef(Type type, Id id, Expression initValue, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.initValue = initValue;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Definirea unei functii
class FuncDef extends Definition {
    Type type;
    Id id;
    LinkedList<FormalDef> formalDefs;
    Block body;

    FuncDef(Type type, Id id, LinkedList<FormalDef> formalDefs, Block body, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.formalDefs = formalDefs;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Block extends Expression {
    LinkedList<ASTNode> stmts;

    Block(LinkedList<ASTNode> stmts, Token token) {
        super(token);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Program extends ASTNode {
    LinkedList<ASTNode> stmts;

    Program(LinkedList<ASTNode> stmts, Token token) {
        super(token);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
