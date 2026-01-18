package optim;

import analysis.DominatorTree;
import analysis.PostOrderNumbering;
import irgen.*;

import java.util.*;

/**
 * Eliminarea fluxului de control inutil
 */
public class UselessControlFlowElimination {
    protected CFG cfg;

    public UselessControlFlowElimination(CFG cfg) {
        this.cfg = cfg;
    }

    /**
     * Dacă un bloc se termină cu un salt condiționat în care ambele ținte sunt același bloc, saltul poate fi înlocuit
     * cu unul necondiționat.
     *
     * @param block blocul curent
     */
    public void foldRedundantBranch(BasicBlock block) {
        // Obținem unicul succesor
        var successor = block.successors.getFirst();

        // Rescriem saltul din condiționat în necondiționat
        block.instructions.getLast().operands = Collections.singletonList(successor);

        // Eliminăm una dintre cele două legături către succesor
        block.removeSuccessor(successor);
    }

    /**
     * Înlătură un bloc gol din punct de vedere logic, care se termină într-un salt necondiționat către un succesor unic
     * și mai conține cel mult instrucțiuni ϕ. Se refac legăturile bidirecționale dintre predecesorii blocului gol
     * și succesorul acestuia. În final, este posibil ca succesorul să capete mai mult de doi predecesori: predecesorii
     * existenți, cu excepția blocului eliminat, plus predecesorii blocului eliminat.
     *
     * În cazul în care succesorul conține instrucțiuni ϕ care referă blocul eliminat, operanzii valoare și bloc
     * respectivi trebuie actualizați pentru a indica predecesorii blocului eliminat, în locul blocului eliminat.
     * În cazul în care blocul eliminat are mai mulți predecesori, valoarea din instrucțiunea ϕ aferentă blocului
     * eliminat capătă o multiplicitate egală cu numărul de predecesori ai blocului eliminat.
     *
     * De exemplu, să presupunem că blocul block4 are predecesorii block2 și block3, și că include instrucțiunea
     * ... = ϕ v2 v3 block2 block3, iar block2 are ca predecesori blocurile block0 și block1. Dacă dorim să eliminăm
     * block2 ca block vid, noua formă a instrucțiunii din block4 va fi ... = ϕ v2 v2 v3 block0 block1 block3.
     *
     * Vedeți și explicațiile metodei BasicBlock.addSuccessor(BasicBlock, BasicBlock), cu doi parametri.
     *
     * @param emptyBlock blocul gol
     */
    public void removeEmptyBlock(BasicBlock emptyBlock) {
        // Obținem unicul succesor
        var successor = emptyBlock.successors.getFirst();

        // Ținta saltului necondiționat devine succesorul predecesorilor blocului eliminat. Noii operanzi
        // ai instrucțiunilor ϕ din succesor, aferenți predecesorilor blocului eliminat, preiau valorile de la blocul
        // eliminat.
        emptyBlock.predecessors.stream()
                .toList()
                .forEach(predecessor -> predecessor.replaceSuccessor(emptyBlock, successor));

        // Blocul curent nu mai este predecesor al succesorului
        emptyBlock.removeSuccessor(successor);

        // Dacă blocul eliminat este cel de intrare în CFG, punem succesorul unic în locul său pe prima poziție
        // din lista de blocuri. Altfel, transformările ulterioare (e.g. unreachable code elimination) ar porni eronat
        // de la blocul de intrare eliminat.
        if (emptyBlock == cfg.blocks.getFirst()) {
            cfg.blocks.remove(successor);
            cfg.blocks.addFirst(successor);
        }
    }

    /**
     * Verifică dacă blocul curent poate fi combinat cu alt bloc. Acest lucru este posibil numai dacă blocul curent
     * are un unic succesor, iar acesta are doar blocul curent ca unic predecesor. Dacă proprietatea anterioară este
     * satisfăcută, succesorul preia instrucțiunile blocului curent, care devine gol și poate fi eliminat.
     *
     * @param block blocul de analizat
     */
    public void combineBlocks(BasicBlock block) {
        // Obținem unicul succesor
        var successor = block.successors.getFirst();

        // Lista instrucțiunilor mutate include toate instrucțiunile blocului curent, cu excepția ultimei, de salt
        var movedInstructions = block.instructions.subList(0, block.instructions.size() - 1);

        // Noua listă de instrucțiuni din blocul curent o mai include doar pe ultima, de salt. Această modificare este
        // necesară pentru a preveni efecte nedorite ale înlăturării blocului curent ca bloc gol, asupra instrucțiunilor
        // mutate în succesor.
        block.instructions = block.instructions.subList(block.instructions.size() - 1, block.instructions.size());

        // Succesorul absoarbe instrucțiunile blocului curent, care devine gol și poate fi înlăturat
        removeEmptyBlock(block);

        // Actualizăm blocul conținător al instrucțiunilor mutate (câmpul containingBlock)
        movedInstructions.forEach(instruction -> instruction.containingBlock = successor);

        // Adăugăm instrucțiunile mutate în succesor, înaintea instrucțiunilor existente
        successor.instructions.addAll(0, movedInstructions);
    }

    public void hoistBranch(BasicBlock block) {
        // Obținem saltul necondiționat de la finalul blocului
        var branch = block.instructions.getLast();

        // Obținem unicul succesor
        var successor = block.successors.getFirst();

        // Obținem saltul condiționat de la finalul succesorului
        var successorBranch = successor.instructions.getLast();

        // Creăm o copie a saltului condiționat din succesor și o „ridicăm” la finalul blocul curent, în locul saltului
        // necondiționat
        branch.operands = new ArrayList<>(successorBranch.operands);

        // Cele două ținte ale saltului condiționat din succesor devin succesorii blocului curent. Operanzii
        // eventualelor instrucțiuni ϕ din cele două ținte, aferenți blocului curent, preiau valorile de la succesor.
        block.addSuccessor((BasicBlock) successorBranch.operands.get(1), successor);
        block.addSuccessor((BasicBlock) successorBranch.operands.get(2), successor);

        // Blocul curent pierde succesorul anterior
        block.removeSuccessor(successor);
    }

    /**
     * Rulează o iterație pe întregul CFG.
     *
     * @return true, dacă a putut fi aplicată vreo transformare asupra unui bloc
     */
    public boolean run() {
        var postOrderNumbering = new PostOrderNumbering(cfg);
        postOrderNumbering.computePostOrderNumbers();

        // Arborele de postdominanță este arborele de dominanță al CFG-ului inversat (cu arcele inversate)
        cfg.reverse();

        var postDominatorTree = new DominatorTree(cfg);
        postDominatorTree.computeDominanceFrontiers();

        cfg.reverse();

        var changed = false;

        // Parcurgem blocurile în postordine
        for (var block : postOrderNumbering.postOrder()) {
            var lastInstruction = block.instructions.getLast();

            // Dacă blocul se termină cu un salt condiționat:
            if (lastInstruction.isConditionalBranch())
                // FOLD REDUNDANT BRANCH:
                // Dacă cei doi succesori sunt identici, putem utiliza în loc un salt necondiționat
                if (lastInstruction.operands.get(1) == lastInstruction.operands.get(2)) {
                    System.out.println("Fold redundant branch: "
                            + block.getDisplayName()
                            + " -> "
                            + lastInstruction.operands.get(1).getDisplayName());

                    foldRedundantBranch(block);
                    changed = true;
                }

            // Dacă blocul se termină cu un salt necondiționat (observați că saltul necondiționat poate fi obținut
            // prin transformarea saltului condiționat mai sus, deci acest caz nu este exclusiv cu anteriorul):
            if (lastInstruction.isUnconditionalBranch()) {
                // Unicul succesor
                var successor = block.successors.getFirst();

                // REMOVE EMPTY BLOCK:
                // Dacă blocul este logic gol și fie nu este sursa vreunei instrucțiuni ϕ, fie este dar își postdomină
                // toți predecesorii, îl eliminăm
                if (block.isLogicallyEmpty()
                        && (! block.isPhiSource() || block.postDominatesPredecessors(postDominatorTree))) {
                    System.out.println("Remove empty block: "
                            + block.getDisplayName()
                            + " -> "
                            + successor.getDisplayName());

                    // Dacă succesorul are un singur predecesor, este mai simplu să privim ca pe un caz de combinare
                    // de blocuri, pentru a copia și eventualele instrucțiuni ϕ din blocul logic gol
                    if (successor.predecessors.size() == 1)
                        combineBlocks(block);
                    // Dacă succesorul are mai mulți predecesori, eventualele instrucțiuni ϕ din blocul logic gol vor
                    // avea instrucțiuni ϕ corespondente în succesor, și vor fi integrate în acestea din urmă
                    else
                        removeEmptyBlock(block);

                    changed = true;
                }
                // COMBINE BLOCKS:
                // Dacă blocul nu este gol și unicul succesor are un unic predecesor, combinăm blocurile
                else if (successor.predecessors.size() == 1) {
                    System.out.println("Combine blocks: "
                            + block.getDisplayName()
                            + " -> "
                            + successor.getDisplayName());

                    combineBlocks(block);
                    changed = true;
                }
                // HOIST BRANCH:
                // Dacă succesorul blocului conține doar un salt condiționat, înlocuim saltul necondiționat de la finalul
                // blocului curent cu o copie a saltului condiționat de la finalul succesorului, evitând astfel două
                // salturi consecutive
                else if (successor.instructions.stream().allMatch(Instruction::isConditionalBranch)) {
                    System.out.println("Hoist branch: "
                            + block.getDisplayName()
                            + " -> "
                            + successor.getDisplayName());

                    hoistBranch(block);
                    changed = true;
                }
            }
        }

        return changed;
    }
}
