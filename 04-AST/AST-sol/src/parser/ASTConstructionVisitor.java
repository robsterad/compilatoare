package parser;
import java.util.*;

public class ASTConstructionVisitor extends CPLangParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitId(CPLangParser.IdContext ctx) {
        return new Id(ctx.ID().getSymbol());
    }

    @Override
    public ASTNode visitInt(CPLangParser.IntContext ctx) {
        return new IntLiteral(ctx.INT().getSymbol());
    }

    @Override
    public ASTNode visitIf(CPLangParser.IfContext ctx) {
        return new If((Expression)visit(ctx.cond),
                (Expression)visit(ctx.thenBranch),
                (Expression)visit(ctx.elseBranch),
                ctx.start);
    }

    @Override
    public ASTNode visitFor(CPLangParser.ForContext ctx) {
        return new For(
                (Expression)visit(ctx.init),
                (Expression)visit(ctx.cond),
                (Expression)visit(ctx.step),
                (Expression)visit(ctx.body),
                ctx.start);
    }

    @Override
    public ASTNode visitFloat(CPLangParser.FloatContext ctx) {
        return new FloatLiteral(ctx.FLOAT().getSymbol());
    }

    @Override
    public ASTNode visitBool(CPLangParser.BoolContext ctx) {
        return new BoolLiteral(ctx.BOOL().getSymbol());
    }

    @Override
    public ASTNode visitAssign(CPLangParser.AssignContext ctx) {
        return new Assign(new Id(ctx.name),
                (Expression)visit(ctx.e),
                ctx.ASSIGN().getSymbol());
    }

    @Override
    public ASTNode visitRelational(CPLangParser.RelationalContext ctx) {
        return new Relational((Expression)visit(ctx.left),
                (Expression)visit(ctx.right),
                ctx.op);
    }

    @Override
    public ASTNode visitArithmetic(CPLangParser.ArithmeticContext ctx) {
        return new Arithmetic((Expression)visit(ctx.left),
                (Expression)visit(ctx.right),
                ctx.op);
    }

    @Override
    public ASTNode visitUnaryMinus(CPLangParser.UnaryMinusContext ctx) {
        return new UnaryMinus((Expression)visit(ctx.e),
                ctx.MINUS().getSymbol());
    }

    @Override
    public ASTNode visitParen(CPLangParser.ParenContext ctx) {
        return visit(ctx.e);
    }

    @Override
    public ASTNode visitCall(CPLangParser.CallContext ctx) {
        LinkedList<Expression> args = new LinkedList<>();
        for (var child : ctx.args) {
            args.add((Expression)visit(child));
        }

        return new Call(new Id(ctx.name),
                args,
                ctx.start);
    }

    @Override
    public ASTNode visitFormal(CPLangParser.FormalContext ctx) {
        return new FormalDef(new Type(ctx.type), new Id(ctx.name), ctx.start);
    }

    @Override
    public ASTNode visitVarDef(CPLangParser.VarDefContext ctx) {
        if (ctx.init != null)
            return new GlobalVarDef(
                    new Type(ctx.type),
                    new Id(ctx.name),
                    (Expression)visit(ctx.init),
                    ctx.start
            );
        else
            return new GlobalVarDef(
                    new Type(ctx.type),
                    new Id(ctx.name),
                    null,
                    ctx.start
            );
    }

    @Override
    public ASTNode visitFuncDef(CPLangParser.FuncDefContext ctx) {
        LinkedList<FormalDef> formalDefs = new LinkedList<>();
        for (var formal : ctx.formals)
            formalDefs.add((FormalDef)visit(formal));

        return new FuncDef(
                new Type(ctx.type),
                new Id(ctx.name),
                formalDefs,
                (Block)visit(ctx.body),
                ctx.start
        );
    }

    @Override
    public ASTNode visitLocal(CPLangParser.LocalContext ctx) {
        Expression initExpr = null;

        if (ctx.init != null)
            initExpr = (Expression)visit(ctx.init);

        return new LocalVarDef(
                new Type(ctx.type),
                new Id(ctx.name),
                initExpr,
                ctx.start
        );
    }

    @Override
    public ASTNode visitBlock(CPLangParser.BlockContext ctx) {
        LinkedList<ASTNode> stmts = new LinkedList<>();
        for (var child : ctx.children) {
            ASTNode stmt = visit(child);
            if (stmt != null)
                stmt.debugStr = child.getText();

            if (stmt != null) {
                stmts.add(stmt);
            }
        }

        return new Block(stmts, ctx.start);
    }

    @Override
    public ASTNode visitProg(CPLangParser.ProgContext ctx) {
        LinkedList<ASTNode> stmts = new LinkedList<>();
        for (var child : ctx.children) {
            ASTNode stmt = visit(child);
            if (stmt != null)
                stmt.debugStr = child.getText();

            if (stmt != null) {
                stmts.add(stmt);
            }
        }

        return new Program(stmts, ctx.start);
    }
}