import java.io.IOException;

import analysis.DominatorAnalysis;
import analysis.DominatorTree;
import analysis.GlobalConstantAnalysis;
import analysis.LivenessAnalysis;
import irgen.IRGenVisitor;
import irgen.SSATransformer;
import lexer.CPLangLexer;
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

//        // Analiza globală a constantelor
//        var fGlobalConstantAnalysis = new GlobalConstantAnalysis(fCfg);
//        fGlobalConstantAnalysis.runForward();
//        System.out.println(fGlobalConstantAnalysis);

        // Analiza duratei de viață a valorilor
        var fLivenessAnalysis = new LivenessAnalysis(fCfg);
        fLivenessAnalysis.runBackwards();
//        System.out.println(fLivenessAnalysis);

        // Analiza de dominanță
        var fDominatorAnalysis = new DominatorAnalysis(fCfg);
        fDominatorAnalysis.runForward();
//        System.out.println(fDominatorAnalysis);

        // Arborele de dominanță
        var fDominatorTree = new DominatorTree(fDominatorAnalysis);
//        System.out.println(fDominatorTree);
//        fDominatorTree.toDOTFile("f-DT.dot");

        // Transformarea în forma SSA
        var fSSATransformer = new SSATransformer(fDominatorTree, fLivenessAnalysis);
        fSSATransformer.insertPhiInstructions();
        fSSATransformer.rename();

        fCfg.toDOTFile("f-SSA.dot");

        String name = "f-SSA";

        // Numerotarea globală a valorilor
        var globalValueNumbering = new GlobalValueNumbering(fCfg);

        // Eliminarea codului neexecutabil
        var unreachableCodeElimination = new UnreachableCodeElimination(fCfg);

        // Multiple runde
        for (int i = 1; i <= 3; i++) {
            globalValueNumbering.propagate();

            unreachableCodeElimination.processConditionalBranches();
            unreachableCodeElimination.computeReachableBlocks();
            unreachableCodeElimination.cleanup();
        }

        fCfg.toDOTFile("f-SSA-GVN-UCE.dot");

        // Eliminarea codului inutil
        var uselessCodeElimination = new UselessCodeElimination(fCfg);
        uselessCodeElimination.computeUsefulInstructions();
        uselessCodeElimination.cleanup();

        // Afișarea instrucțiunilor utile
//        System.out.println(uselessCodeElimination.usefulInstructions.stream()
//                .map(instruction -> instruction.toString() + " @ " + instruction.containingBlock.getDisplayName())
//                .collect(Collectors.joining("\n")));

        fCfg.toDOTFile("f-SSA-UsCE-reference.dot");

        // Eliminarea codului neexecutabil
        unreachableCodeElimination.processConditionalBranches();
        unreachableCodeElimination.computeReachableBlocks();
        unreachableCodeElimination.cleanup();

        fCfg.toDOTFile("f-SSA-UsCE-UCE-reference.dot");

        // Eliminarea fluxului de control inutil
        var uselessControlFlowElimination = new UselessControlFlowElimination(fCfg);
        uselessControlFlowElimination.run();

        fCfg.toDOTFile("f-SSA-UsCE-UCE-UCFE-reference.dot");

        // Eliminarea codului neexecutabil
        unreachableCodeElimination.processConditionalBranches();
        unreachableCodeElimination.computeReachableBlocks();
        unreachableCodeElimination.cleanup();

        fCfg.toDOTFile("f-SSA-UsCE-UCE-UCFE-UCE-reference.dot");
    }

}
