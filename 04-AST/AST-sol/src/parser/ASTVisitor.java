package parser;
import org.antlr.v4.runtime.*;

public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(IntLiteral intLiteral);
    T visit(FloatLiteral floaty);
    T visit(BoolLiteral boolLiteral);
    T visit(UnaryMinus uMinus);
    T visit(Arithmetic arithmetic);
    T visit(Assign assign);
    T visit(GlobalVarDef globalVarDef);
    T visit(FuncDef funcDef);
    T visit(If iff);
    T visit(For forr);
    T visit(Call call);
    T visit(Relational rel);
    T visit(Type type);
    T visit(FormalDef formalDef);
    T visit(LocalVarDef localVarDef);
    T visit(Block block);
    T visit(Program program);
}
