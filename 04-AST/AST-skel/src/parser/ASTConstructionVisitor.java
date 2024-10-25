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
}