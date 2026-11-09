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

    // TODO 3: Completati cu alte metode pentru a trece din arborele
    // generat automat in reprezentarea AST-ului dorit

    @Override
    public ASTNode visitProg(CPLangParser.ProgContext ctx) {
        var stmts = new LinkedList<ASTNode>();

        // Păstrăm ordinea din program a definițiilor și a expresiilor.
        for (var child : ctx.children) {
            if (child instanceof CPLangParser.DefinitionContext
                    || child instanceof CPLangParser.ExprContext) {
                stmts.add(visit(child));
            }
        }

        return new Program(stmts, ctx.start);
    }

    @Override
    public ASTNode visitVarDef(CPLangParser.VarDefContext ctx) {
        return new GlobalVarDef(ctx.type,
                new Id(ctx.name),
                ctx.init == null ? null : (Expression)visit(ctx.init),
                ctx.start);
    }

    @Override
    public ASTNode visitFuncDef(CPLangParser.FuncDefContext ctx) {
        var formals = new LinkedList<Formal>();
        for (var formal : ctx.formals) {
            formals.add((Formal)visit(formal));
        }

        return new FuncDef(ctx.type,
                new Id(ctx.name),
                formals,
                (Block)visit(ctx.body),
                ctx.start);
    }

    @Override
    public ASTNode visitFormal(CPLangParser.FormalContext ctx) {
        return new Formal(ctx.type, new Id(ctx.name), ctx.start);
    }

    @Override
    public ASTNode visitLocal(CPLangParser.LocalContext ctx) {
        return new LocalVarDef(ctx.type,
                new Id(ctx.name),
                ctx.init == null ? null : (Expression)visit(ctx.init),
                ctx.start);
    }

    @Override
    public ASTNode visitBlock(CPLangParser.BlockContext ctx) {
        var stmts = new LinkedList<ASTNode>();

        for (var child : ctx.children) {
            if (child instanceof CPLangParser.LocalContext
                    || child instanceof CPLangParser.ExprContext) {
                stmts.add(visit(child));
            }
        }

        return new Block(stmts, ctx.start);
    }

    @Override
    public ASTNode visitCall(CPLangParser.CallContext ctx) {
        var args = new LinkedList<Expression>();
        for (var arg : ctx.args) {
            args.add((Expression)visit(arg));
        }

        return new Call(new Id(ctx.name), args, ctx.start);
    }

    @Override
    public ASTNode visitUnaryMinus(CPLangParser.UnaryMinusContext ctx) {
        return new UnaryMinus((Expression)visit(ctx.e), ctx.start);
    }

    @Override
    public ASTNode visitArithmetic(CPLangParser.ArithmeticContext ctx) {
        return new Arithmetic((Expression)visit(ctx.left),
                (Expression)visit(ctx.right),
                ctx.op);
    }

    @Override
    public ASTNode visitRelational(CPLangParser.RelationalContext ctx) {
        return new Relational((Expression)visit(ctx.left),
                (Expression)visit(ctx.right),
                ctx.op);
    }

    @Override
    public ASTNode visitFor(CPLangParser.ForContext ctx) {
        return new For((Expression)visit(ctx.init),
                (Expression)visit(ctx.cond),
                (Expression)visit(ctx.step),
                (Expression)visit(ctx.body),
                ctx.start);
    }

    @Override
    public ASTNode visitAssign(CPLangParser.AssignContext ctx) {
        return new Assign(new Id(ctx.name),
                (Expression)visit(ctx.e),
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

    // Parantezele doar dirijează analiza sintactică; în AST rămâne doar
    // expresia din interior.
    @Override
    public ASTNode visitParen(CPLangParser.ParenContext ctx) {
        return visit(ctx.e);
    }

    @Override
    public ASTNode visitBlockExpr(CPLangParser.BlockExprContext ctx) {
        return visit(ctx.block());
    }
}