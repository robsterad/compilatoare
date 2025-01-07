package optim;

import irgen.BasicBlock;
import irgen.CFG;
import irgen.InstructionType;
import semantic.IntSymbol;

import java.util.HashSet;
import java.util.List;
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
    public void processConditionalBranches() {
        // Parcurgem toate blocurile
        for (var block : cfg.blocks) {
            // TODO Ne interesează doar dacă ultima instrucțiune este salt condiționat: BR conditie blocAdev blocFals

            // TODO Verificăm dacă condiția saltului (primul operand) este o constantă întreagă (0/1)

            // TODO Determinăm blocul pe care îl păstrăm drept țintă și rescriem ca salt necondiționat: BR bloc

            // TODO Parcurgem succesorii blocului curent și îi eliminăm pe cei diferiți de țintă. De asemenea, eliminăm
            //  blocul curent din listele de predecesori ai succesorilor înlăturați.

        }
    }

    /**
     * Determină blocurile executabile, pornind de la blocul de intrare din CFG
     */
    public void computeReachableBlocks() {
        reachableBlocks.clear();
        computeReachableBlocks(cfg.blocks.getFirst());
    }

    /**
     * Determină blocurile executabile pornind de la un bloc fixat
     *
     * @param block blocul de plecare
     */
    protected void computeReachableBlocks(BasicBlock block) {
        // TODO Marcăm blocul curent ca executabil

        // TODO Determinăm blocurile accesibile din blocul curent, evitând ciclurile

    }

    /**
     * Simplifică CFG-ul în funcție de blocurile marcate ca neexecutabile. Acestea sunt înlăturate cu totul, inclusiv
     * din listele de predecesori ale blocurilor executabile care le succed. În final, instrucțiunile ϕ din blocurile
     * executabile în care mai intră un singur predecesor (în urma înlăturării predecesorilor neexecutabili) sunt
     * înlocuite cu instrucțiuni de copiere a versiunii furnizate de predecesorul rămas.
     */
    public void cleanup() {
        // TODO Păstrăm în CFG doar blocurile executabile

        // Parcurgem blocurile executabile, singurele rămase în CFG, după implementarea punctului anterior
        for (var block : cfg.blocks) {
            // TODO Dacă blocul curent are predecesori neexecutabili, îi înlăturăm din lista de predecesori

            // TODO Pentru blocurile cu un singur predecesor, verificăm dacă există instrucțiuni ϕ, de pe vremea când
            //  blocul avea mai mulți predecesori, și le înlocuim cu instrucțiuni de copiere a singurului operand valid
            if (block.predecessors.size() == 1)
                for (var instruction : block.instructions) {
                    if (instruction.iType == InstructionType.PHI) {
                        // TODO
                    }
                }
        }
    }
}
