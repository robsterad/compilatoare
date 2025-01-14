package optim;

import analysis.DominatorAnalysis;
import analysis.DominatorTree;
import irgen.BasicBlock;
import irgen.CFG;
import irgen.Instruction;
import irgen.InstructionType;

import java.util.*;

/**
 * Eliminarea codului inutil
 */
public class UselessCodeElimination {
    protected CFG cfg;

    /**
     * Instrucțiunile marcate ca utile
     */
    protected Set<Instruction> usefulInstructions = new HashSet<>();

    /**
     * Blocurile marcate ca utile, pe baza instrucțiunilor pe utile care le conțin. Înlesnește determinarea facilă a
     * primului postdominator util al unui bloc
     */
    protected Set<BasicBlock> usefulBlocks = new HashSet<>();

    /**
     * Coada de instrucțiuni ce urmează a fi procesate prin prisma utilității
     */
    protected Deque<Instruction> instructionQueue = new ArrayDeque<>();

    /**
     * Arborele de postdominare
     */
    protected DominatorTree postDominatorTree;

    public UselessCodeElimination(CFG cfg) {
        this.cfg = cfg;
    }

    /**
     * Determină instrucțiunile utile din CFG
     */
    public void computeUsefulInstructions() {
        usefulInstructions.clear();
        usefulBlocks.clear();
        instructionQueue.clear();

        // Arborele de postdominare este arborele de dominare al CFG-ului inversat (cu arcele inversate)
        cfg.reverse();

        var postDominatorAnalysis = new DominatorAnalysis(cfg);
        postDominatorAnalysis.runForward();

        postDominatorTree = new DominatorTree(postDominatorAnalysis);

        cfg.reverse();

        // TODO Identificăm toate instrucțiunile critice din CFG (RET și CALL)
        for (var block : cfg.blocks)
            for (var instruction : block.instructions) {
                // TODO Vedeți funcția markUsefulInstruction(Instruction) de mai jos
            }

        // Până la golirea cozii
        while (! instructionQueue.isEmpty()) {
            // Extragem o instrucțiune
            var instruction = instructionQueue.removeFirst();

            // Parcurgem operanzii
            for (var operand : instruction.operands) {
                // TODO Dacă operandul curent corespunde unei alte instrucțiuni, o marcăm pe aceasta ca utilă

                // TODO Pentru instrucțiunile ϕ, ne interesează și blocurile referite, pentru a marca salturile de la
                //  finalului acestora drept utile
            }

            // TODO În final, parcurgem blocurile din frontiera de postdominanță a blocului care conține
            //  instrucțiunea și marcăm salturile de la finalul acestora drept utile

        }
    }

    /**
     * Simplifică CFG-ul, eliminând sau rescriind instrucțiunile inutile
     */
    public void cleanup() {
        // Parcurgem toate blocurile
        for (var block : cfg.blocks)
            // Parcurgem instrucțiunile din blocul curent. Utilizăm un iterator pentru a putea șterge instrucțiuni pe
            // parcursul iterării.
            for (var it = block.instructions.iterator(); it.hasNext(); ) {
                var instruction = it.next();

                // Dacă instrucțiunea este utilă, nu ne atingem de ea
                if (usefulInstructions.contains(instruction))
                    continue;

                // TODO Un salt condiționat inutil trebuie rescris ca unul necondiționat către cel mai apropiat
                //  postdominator util
                if (instruction.isConditionalBranch()) {
                    var postDominator = block;

                    // TODO Determinăm cel mai apropiat postdominator util, consultând arborele de postdominanță

                    // TODO Îl stabilim pe acesta drept unic succesor al blocului curent.
                    //  Vedeți metoda BasicBlock.setUniqueSuccesor(BasicBlock).

                }

                // TODO Orice altă instrucțiune inutilă diferită de un salt este eliminată

            }
    }

    /**
     * Dacă instrucțiunea nu a fost deja marcată drept utilă, marchează instrucțiunea și blocul care o conține.
     * De asemenea, introduce instrucțiunea în coada de procesare.
     *
     * @param instruction instrucțiunea utilă
     */
    protected void markUsefulInstruction(Instruction instruction) {
        if (usefulInstructions.contains(instruction))
            return;

        usefulInstructions.add(instruction);
        usefulBlocks.add(instruction.containingBlock);

        instructionQueue.add(instruction);
    }
}
