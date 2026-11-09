package parser;
import org.antlr.v4.runtime.*;

public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(IntLiteral intLiteral);
    T visit(If iff);
    // TODO 2: Adăugați metode pentru fiecare clasă definită anterior
    T visit(Program program);
    T visit(FloatLiteral floatLiteral);
    T visit(BoolLiteral boolLiteral);
    T visit(UnaryMinus unaryMinus);
    T visit(Arithmetic arithmetic);
    T visit(Relational relational);
    T visit(Assign assign);
    T visit(Call call);
    T visit(For forr);
    T visit(Block block);
    T visit(GlobalVarDef globalVarDef);
    T visit(LocalVarDef localVarDef);
    T visit(Formal formal);
    T visit(FuncDef funcDef);
}
