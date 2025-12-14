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

        var input = CharStreams.fromFileName("gvn.txt");

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

        // CFG-ul funcției "f"
        var fCfg = irGenVisitor.functionSymbols.get("f").cfg;

//        var postOrderNumbering = new PostOrderNumbering(fCfg);
//        postOrderNumbering.computePostOrderNumbers();
//        System.out.println(postOrderNumbering);

//        for (var block : postOrderNumbering.reversePostOrder())
//            System.out.println(block.getDisplayName() + ": " + postOrderNumbering.getReversePostOrderNumber(block));


//        // Analiza globală a constantelor
//        var fGlobalConstantAnalysis = new GlobalConstantAnalysis(fCfg);
//        fGlobalConstantAnalysis.runForward();
//        // System.out.println(fGlobalConstantAnalysis);

        // Analiza duratei de viață a valorilor
        var fLivenessAnalysis = new LivenessAnalysis(fCfg);
        fLivenessAnalysis.runBackwards();
//        System.out.println(fLivenessAnalysis);

//        // Analiza de dominanță
//        var fDominatorAnalysis = new DominatorAnalysis(fCfg);
//        fDominatorAnalysis.runForward();
//        System.out.println(fDominatorAnalysis);

        // Analiza de dominanță imediată
        var fImmediateDominatorAnalysis = new ImmediateDominatorAnalysis(fCfg);
        fImmediateDominatorAnalysis.runForward();
//        System.out.println(fImmediateDominatorAnalysis);

        // Arborele de dominanță
        var fDominatorTree = new DominatorTree(fCfg);
        fDominatorTree.computeDominanceFrontiers();
//        System.out.println(fDominatorTree);
        fDominatorTree.toDOTFile("f-DT.dot");

        // Transformarea în forma SSA
        var fSSATransformer = new SSATransformer(fCfg);
        fSSATransformer.insertPhiInstructions();
        fSSATransformer.rename();

        fCfg.toDOTFile("f-SSA.dot");

        String name = "f-SSA";

        var globalValueNumbering = new GlobalValueNumbering(fCfg);
        var unreachableCodeElimination = new UnreachableCodeElimination(fCfg);

        // Aplicare repetată a optimizărilor de mai sus, până la absența modificărilor
        int iterations = 0;
        boolean gvnChanged, uceChanged = false;

        do {
            // Numerotarea globală a valorilor
            gvnChanged = globalValueNumbering.run();

            // Doar dacă s-a modificat ceva după prima optimizare, are rost să scriem un nou fișier și să încercăm
            // și a doua optimizare
            if (gvnChanged) {
                iterations++;

                name += "-GVN";
                fCfg.toDOTFile(name + ".dot");

                // Eliminarea codului neexecutabil
                uceChanged = unreachableCodeElimination.run();

                if (uceChanged) {
                    name += "-UCE";
                    fCfg.toDOTFile(name + ".dot");
                }
            }
        } while (gvnChanged || uceChanged);

        System.out.println("Finished after " + iterations + " iteration(s)");
    }

}
