import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import analysis.*;
import irgen.*;
import org.antlr.v4.runtime.*;
import semantic.*;
import parser.*;
import optim.*;

public class Test {

    public static void main(String[] args) throws IOException {

        var input = CharStreams.fromFileName("elimination.txt");

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

        // TODO Funcția de interes
        String FUNCTION_NAME = "f";

        // CFG-ul funcției de interes
        var cfg = irGenVisitor.functionSymbols.get(FUNCTION_NAME).cfg;

        // Numerotare în postordine
//        var postOrderNumbering = new PostOrderNumbering(cfg);
//        postOrderNumbering.computePostOrderNumbers();
//        System.out.println(postOrderNumbering);

//        for (var block : postOrderNumbering.reversePostOrder())
//            System.out.println(block.getDisplayName() + ": " + postOrderNumbering.getReversePostOrderNumber(block));

        // Analiza globală a constantelor
//        var globalConstantAnalysis = new GlobalConstantAnalysis(cfg);
//        globalConstantAnalysis.runForward();
//        System.out.println(globalConstantAnalysis);

        // Analiza duratei de viață a valorilor
//        var livenessAnalysis = new LivenessAnalysis(cfg);
//        livenessAnalysis.runBackwards();
//        System.out.println(livenessAnalysis);

//        // Analiza de dominanță
//        var dominatorAnalysis = new DominatorAnalysis(cfg);
//        dominatorAnalysis.runForward();
//        System.out.println(dominatorAnalysis);

        // Analiza de dominanță imediată
//        var immediateDominatorAnalysis = new ImmediateDominatorAnalysis(cfg);
//        immediateDominatorAnalysis.runForward();
//        System.out.println(immediateDominatorAnalysis);

        // Arborele de dominanță
        var dominatorTree = new DominatorTree(cfg);
        dominatorTree.computeDominanceFrontiers();
//        System.out.println(dominatorTree);
        dominatorTree.toDOTFile(FUNCTION_NAME + "-DT.dot");

        // Transformarea în forma SSA
        var ssaTransformer = new SSATransformer(cfg);
        ssaTransformer.insertPhiInstructions();
        ssaTransformer.rename();

        String fileName = FUNCTION_NAME + "-SSA";

        cfg.toDOTFile(fileName + ".dot");

//        var entryBlock = cfg.blocks.getFirst();
//
//        System.out.println("Initial entry block:\n" + entryBlock);
//
//        var valueNumbering = new ValueNumbering();
//        valueNumbering.run(entryBlock);
//
//        System.out.println("\nAfter Value Numbering:\n" + entryBlock);
//        // Afișarea tabelul utilizat pe parcurs
//        System.out.println("\nValue Numbering table\n" + valueNumbering);

        var globalValueNumbering = new GlobalValueNumbering(cfg);
        var unreachableCodeElimination = new UnreachableCodeElimination(cfg);

        // Aplicare repetată a optimizărilor până la absența modificărilor

        // ----- Global value numbering + unreachable code elimination -----

        int iterations = 0;
        boolean gvnChanged, uceChanged;

        do {
            // Numerotarea globală a valorilor
            gvnChanged = globalValueNumbering.run();

            // Doar dacă s-a modificat ceva după prima optimizare, are rost să scriem un nou fișier și să încercăm
            // și a doua optimizare
            if (gvnChanged) {
                iterations++;

                fileName += "-GVN";
                cfg.toDOTFile(fileName + ".dot");

                // Eliminarea codului neexecutabil
                uceChanged = unreachableCodeElimination.run();

                if (uceChanged) {
                    fileName += "-UCE";
                    cfg.toDOTFile(fileName + ".dot");
                }
            }
        } while (gvnChanged);

        System.out.println("Performed " + iterations + " GVN/UCE iteration(s)");

        // ----- Useless code elimination + unreachable code elimination -----

        var uselessCodeElimination = new UselessCodeElimination(cfg);

        // Eliminarea codului inutil
        var usceChanged = uselessCodeElimination.run();

        if (usceChanged) {
            System.out.println("Performed 1 UsCE/UCE iteration");

            fileName += "-UsCE";
            cfg.toDOTFile(fileName + ".dot");

            // Eliminarea codului neexecutabil
            uceChanged = unreachableCodeElimination.run();

            if (uceChanged) {
                fileName += "-UCE";
                cfg.toDOTFile(fileName + ".dot");
            }
        }

        // ----- Useless control flow elimination + unreachable code elimination -----

        var uselessControlFlowElimination = new UselessControlFlowElimination(cfg);
        iterations = 0;

        boolean uscfeChanged;

        do {
            // Eliminarea fluxului de control inutil
            uscfeChanged = uselessControlFlowElimination.run();

            if (uscfeChanged) {
                iterations++;

                fileName += "-UsCFE";
                cfg.toDOTFile(fileName + ".dot");

                // Eliminarea codului neexecutabil
                uceChanged = unreachableCodeElimination.run();

                if (uceChanged) {
                    fileName += "-UCE";
                    cfg.toDOTFile(fileName + ".dot");
                }
            }
        } while (uscfeChanged);

        System.out.println("Performed " + iterations + " UsCFE/UCE iteration(s)");
    }

}
