package analysis;

import irgen.BasicBlock;
import irgen.CFG;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Etichetează blocurile din CFG cu numerele de ordine aferente unei parcurgeri în post-ordine.
 */
public class PostOrderNumbering {
    protected CFG cfg;

    /**
     * Un LinkedHashMap permite iterarea în ordinea introducerii cheilor, în vederea reconstituirii parcurgerii.
     */
    protected LinkedHashMap<BasicBlock, Integer> postOrderNumbers = new LinkedHashMap<>();

    protected Set<BasicBlock> visited = new HashSet<>();

    protected int number;

    public PostOrderNumbering(CFG cfg) {
        this.cfg = cfg;
    }

    /**
     * Determină numerotarea în post-ordine pentru întregul CFG.
     */
    public void computePostOrderNumbers() {
        // Reinițializare structuri
        postOrderNumbers.clear();
        visited.clear();
        number = 0;

        computePostOrderNumbers(cfg.blocks.getFirst());
    }

    /**
     * Determină numerotarea în post-ordine pornind de la un anumit nod
     *
     * @param block nodul de pornire
     */
    protected void computePostOrderNumbers(BasicBlock block) {
        visited.add(block);

        // Se vizitează mai întâi succesorii
        for (var successor : block.successors)
            // Evitare cicluri
            if (! visited.contains(successor))
                computePostOrderNumbers(successor);

        // Și apoi nodul curent
        postOrderNumbers.put(block, number++);
    }

    /**
     * Determină numărul de post-ordine a unui bloc
     *
     * @param  block blocul de interes
     * @return numărul blocului
     */
    public int getPostOrderNumber(BasicBlock block) {
        return postOrderNumbers.get(block);
    }

    /**
     * Determină numărul de post-ordine inversă a unui bloc
     *
     * @param  block blocul de interes
     * @return       numărul blocului
     */
    public int getReversePostOrderNumber(BasicBlock block) {
        return cfg.blocks.size() - 1 - getPostOrderNumber(block);
    }

    /**
     * Întoarce un iterator care enumeră blocurile în post-ordine.
     *
     * @return iteratorul
     */
    public SequencedSet<BasicBlock> postOrder() {
        return postOrderNumbers.sequencedKeySet();
    }

    /**
     * Întoarce un iterator care enumeră blocurile în post-ordine inversă.
     *
     * @return iteratorul
     */
    public SequencedSet<BasicBlock> reversePostOrder() {
        return postOrder().reversed();
    }

    /**
     * Construiește un comparator de blocuri pe baza numerelor de post-ordine
     *
     * @return comparatorul
     */
    public Comparator<BasicBlock> getPostOrderComparator() {
        return Comparator.comparing(postOrderNumbers::get);
    }

    /**
     * Construiește un comparator de blocuri pe baza numerelor de post-ordine inversă
     *
     * @return comparatorul
     */
    public Comparator<BasicBlock> getReversePostOrderComparator() {
        return getPostOrderComparator().reversed();
    }

    @Override
    public String toString() {
        return postOrderNumbers.entrySet().stream()
                .map(entry -> entry.getKey().getDisplayName() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }
}
