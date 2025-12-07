import java.io.IOException;

import analysis.*;
import irgen.IRGenVisitor;
import irgen.SSATransformer;
import org.antlr.v4.runtime.*;
import semantic.*;
import parser.*;
import optim.*;

public class Test {

    public static void main(String[] args) throws IOException {

        var input = CharStreams.fromFileName("lvn.txt");

        var lexer = new CPLangLexer(input);
        var tokenStream = new CommonTokenStream(lexer);

        /*
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();
        for (var token : tokens) {
            var text = token.getText();
            var type = CPLangLexer.VOCABULARY.getSymbolicName(token.getType());

            System.out.println(text + " : " + type);
        }
        */

        var parser = new CPLangParser(tokenStream);
        var tree = parser.prog();
//        System.out.println(tree.toStringTree(parser));

        // Visitor-ul de mai jos parcurge arborele de derivare și construiește
        // un arbore de sintaxă abstractă (AST).
        var astConstructionVisitor = new ASTConstructionVisitor();

        // ast este AST-ul proaspăt construit pe baza arborelui de derivare.
        var ast = astConstructionVisitor.visit(tree);

        // În vederea gestiunii referirilor anticipate, utilizăm două treceri,
        // una de definire a simbolurilor, și cealaltă, de rezolvare.
        var definitionPassVisitor = new DefinitionPassVisitor();

        // A doua trecere, pentru rezolvarea simbolurilor în raport cu domeniile
        // de vizibilitate memorate în prima trecere. Observați că, în această
        // trecere, nu mai este necesară gestiunea domeniului curent,
        // ca în prima trecere.
        var resolutionPassVisitor = new ResolutionPassVisitor();

        ast.accept(definitionPassVisitor);
        ast.accept(resolutionPassVisitor);

        // Generarea de cod intermediar
        var irGenVisitor = new IRGenVisitor();
        ast.accept(irGenVisitor);

        // CFG pentru corpul principal al programului
        var mainCfg = irGenVisitor.mainCfg;
        mainCfg.toDOTFile("main.dot");

        // CFG-uri pentru fiecare funcție
        for (var functionSymbol : irGenVisitor.functionSymbols.values())
            functionSymbol.cfg.toDOTFile(functionSymbol.getName() + ".dot");

        // CFG-ul funcției simple
        var simpleCfg = irGenVisitor.functionSymbols.get("simple").cfg;

        // Analiza duratei de viață a valorilor
        var fLivenessAnalysis = new LivenessAnalysis(simpleCfg);
        fLivenessAnalysis.runBackwards();
//        System.out.println(fLivenessAnalysis);

        // Analiza de dominanță imediată
        var fImmediateDominatorAnalysis = new ImmediateDominatorAnalysis(simpleCfg);
        fImmediateDominatorAnalysis.runForward();
//        System.out.println(fImmediateDominatorAnalysis);

        // Arborele de dominanță
        var fDominatorTree = new DominatorTree(fImmediateDominatorAnalysis);
        fDominatorTree.computeDominanceFrontiers();
//        System.out.println(fDominatorTree);
//        fDominatorTree.toDOTFile("f-DT.dot");

        // Transformarea în forma SSA
        var fSSATransformer = new SSATransformer(fDominatorTree, fLivenessAnalysis);
        fSSATransformer.insertPhiInstructions();
        fSSATransformer.rename();

        simpleCfg.toDOTFile("simple-SSA.dot");

        // Value numbering aplicat pe blocul de intrare al funcției simple
        var entryBlock = simpleCfg.blocks.getFirst();

        System.out.println("Initial entry block:\n" + entryBlock);

        var valueNumbering = new ValueNumbering();
        valueNumbering.run(entryBlock);

        System.out.println("\nAfter Value Numbering:\n" + entryBlock);

        // Afișarea tabelul utilizat pe parcurs
        System.out.println("\nValue Numbering table\n" + valueNumbering);
    }

}
