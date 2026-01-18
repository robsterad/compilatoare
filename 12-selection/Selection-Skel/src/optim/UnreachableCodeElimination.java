package optim;

import irgen.BasicBlock;
import irgen.CFG;

import semantic.IntSymbol;

import java.util.HashSet;
import java.util.Set;

/**
 * Eliminarea codului neexecutabil
 */
public class UnreachableCodeElimination {
    protected CFG cfg;

    public Set<BasicBlock> reachableBlocks = new HashSet<>();

    public UnreachableCodeElimination(CFG cfg) {
        this.cfg = cfg;
    }

    /**
     * Verifică dacă salturile condiționate de la finalul blocurilor au condiții constante. În caz afirmativ, rescrie
     * saltul cu unul necondiționat către unicul succesor valabil, înlătură ceilalți succesori ai blocului curent,
     * și înlătură blocul curent dintre predecesorii succesorilor eliminați.
     */
    protected void processConditionalBranches() {
        // Parcurgem toate blocurile
        for (var block : cfg.blocks) {
            // TODO Ne interesează doar dacă ultima instrucțiune este salt condiționat: BR conditie blocAdev blocFals
            var branch = block.instructions.getLast();
            if (! branch.isConditionalBranch())
                continue;

            // TODO Verificăm dacă condiția saltului (primul operand) este o constantă întreagă (0/1)
            var condition = branch.operands.getFirst();
            if (! (condition instanceof IntSymbol))
                continue;

            // TODO Determinăm blocul pe care îl păstrăm drept țintă
            var conditionValue = ((IntSymbol) condition).value;
            var keptSuccessor = (BasicBlock) branch.operands.get(2 - conditionValue);

            // TODO Stabilim ținta drept unic succesor al blocului curent.
            //  Vedeți metoda BasicBlock.setUniqueSuccesor(BasicBlock).
            block.setUniqueSuccessor(keptSuccessor);
        }
    }

    /**
     * Determină blocurile executabile, pornind de la blocul de intrare din CFG
     *
     * @return true, dacă au fost descoperite blocuri neexecutabile
     */
    protected boolean computeReachableBlocks() {
        reachableBlocks.clear();
        computeReachableBlocks(cfg.blocks.getFirst());

        // Dacă numărul blocurilor executabile diferă de cel total, înseamnă că există blocuri neexecutabile
        return reachableBlocks.size() != cfg.blocks.size();
    }

    /**
     * Determină blocurile executabile pornind de la un bloc fixat
     *
     * @param block blocul de plecare
     */
    protected void computeReachableBlocks(BasicBlock block) {
        // TODO Marcăm blocul curent ca executabil
        reachableBlocks.add(block);

        // TODO Determinăm blocurile accesibile din blocul curent, evitând ciclurile
        for (var successor : block.successors)
            if (! reachableBlocks.contains(successor))
                computeReachableBlocks(successor);
    }

    /**
     * Simplifică CFG-ul în funcție de blocurile marcate ca neexecutabile. Acestea sunt înlăturate cu totul, inclusiv
     * din listele de predecesori ale blocurilor executabile care le succed. În final, instrucțiunile ϕ din blocurile
     * executabile în care mai intră un singur predecesor (în urma înlăturării predecesorilor neexecutabili) sunt
     * înlocuite cu instrucțiuni de copiere a versiunii furnizate de predecesorul rămas.
     */
    protected void removeUnreachableBlocks() {
        // TODO Păstrăm în CFG doar blocurile executabile
        cfg.blocks.retainAll(reachableBlocks);

        // TODO Parcurgem blocurile executabile
        for (var block : cfg.blocks) {
            // TODO Dacă blocul curent are predecesori neexecutabili, distrugem legăturile
            block.predecessors.stream()
                    .filter(predecessor -> ! reachableBlocks.contains(predecessor))
                    .toList()
                    .forEach(predecessor -> predecessor.removeSuccessor(block));
        }
    }

    /**
     * Rulează cele trei etape.
     *
     * @return true, dacă s-a modificat CFG-ul
     */
    public boolean run() {
        processConditionalBranches();
        var hasUnreachableBlocks = computeReachableBlocks();

        // Dacă în a doua etapă au fost depistate blocuri neexecutabile, rulăm și a treia etapă
        if (hasUnreachableBlocks)
            removeUnreachableBlocks();

        return hasUnreachableBlocks;
    }
}
