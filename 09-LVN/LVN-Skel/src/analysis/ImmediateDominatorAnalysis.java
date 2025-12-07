package analysis;

import irgen.BasicBlock;
import irgen.CFG;
import irgen.Instruction;
import semantic.Symbol;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Parcurgeți mai întâi clasa DominatorAnalysis!
 *
 * Analiză clasică forward pentru determinarea dominatorului imediat al fiecărui bloc. Este utilizată pentru
 * construcția formei SSA minimale.
 *
 * Un bloc D domină imediat un bloc B dacă D domină strict B și nu există niciun alt dominator strict al lui B dominat
 * strict de D. Cu alte cuvinte, D este „cel mai apropiat” dominator strict al lui B.
 *
 * Abordarea bazată pe dominatori imediați constituie o alternativă mai eficientă, dar echivalentă, la analiza
 * de dominanță bazată pe mulțimi de dominatori (clasa DominatorAnalysis). Din fericire, oricare reprezentare poate fi
 * obținută din cealaltă:
 *
 * 1. Dominatorul imediat al unui nod poate fi determinat pe baza mulțimii tuturor dominatorilor acelui nod.
 * 2. Mulțimea tuturor dominatorilor unui nod se poate obține pornind de la dominatorul imediat și determinând
 *    dominatorul imediat al acestuia ș.a.m.d.
 *
 * Dominatorii imediați codifică implicit arborele de dominanță al CFG-ului (vedeți clasa DominatorTree), în care
 * fiecare bloc are drept părinte dominatorul să imediat, iar blocul de intrare este rădăcina.
 *
 * Ideea de bază a analizei este să reprezentăm mulțimea de dominatori ai unui nod prin dominatorul imediat al acelui
 * nod. Acest lucru ne permite să echivalăm intersecția mulțimilor de dominatori, reprezentate prin dominatori imediați,
 * cu determinarea sufixului comun al căilor ascendente din arborele de dominanță de la fiecare dominator imediat
 * către rădăcină. Pe prima poziție a sufixului comun se află dominatorul imediat care codifică mulțimea rezultată
 * prin intersecție.
 *
 * Determinarea sufixului comun a două căi se poate implementa eficient printr-o urcare simultană în arborele
 * de dominanță, pornind de la cei doi dominatori imediați care codifică mulțimile, până la atingerea primului strămoș
 * comun. Alegerea căii pe care se realizează urcarea într-un pas este determinată de blocul cu numărul de post-ordine
 * inversă mai mare, i.e. situat „mai jos” în CFG.
 *
 * Informația pentru adnotare ar trebui să fie intuitiv un singur bloc, și anume dominatorul imediat, dar utilizăm
 * în loc o listă cu două elemente, din trei motive:
 *
 * 1. Metoda merge necesită pentru fiecare predecesor nu doar informația calculată (dominatorul său imediat), ci și
 *    predecesorul însuși. Prin urmare, informația asociată unui nod este o listă cu două elemente:
 *    [predecesor, dominator imediat al predecesorului].
 * 2. Metoda merge presupune actualizarea distructivă a acumulatorului, și ar fi imposibilă înlocuirea blocului
 *    cu rol de acumulator primit ca parametru cu un altul. Actualizarea distructivă este implementată în schimb
 *    prin înlocuirea elementelor listei.
 * 3. Putem semnala că nu am determinat încă un dominator imediat candidat al unui bloc prin lista vidă.
 *
 * Metoda merge poate ignora predecesorii neprocesați, pentru care nu a fost calculat încă un dominator imediat, fapt
 * denotat de o listă vidă. Această situație corespunde unei intersecții cu mulțimea tuturor nodurilor din CFG,
 * care constituie elementul neutru față de intersecție.
 */
public class ImmediateDominatorAnalysis extends DataflowAnalysis<List<BasicBlock>> {

    public ImmediateDominatorAnalysis(CFG cfg) {
        super(cfg);
    }

    /**
     * La intrarea în blocul de intrare în CFG, informația inițială conține blocul de intrare asociat cu el însuși
     * ca dominator imediat. Deși, principial, un bloc nu se domină imediat pe sine, această inițializare simplifică
     * algoritmul.
     *
     * @return mulțimea [entry, entry].
     */
    @Override
    protected List<BasicBlock> getStartBlockInitialInfo() {
        var entryBlock = cfg.blocks.getFirst();
        return List.of(entryBlock, entryBlock);
    }

    /**
     * Informația inițială a celorlalte blocuri este un dominator imediat absent (încă nedeterminat).
     *
     * @return lista vidă
     */
    @Override
    protected List<BasicBlock> getOtherBlocksInitialInfo() {
        return new LinkedList<>();
    }

    /**
     * Întrucât informația de dominare nu depinde de instrucțiunile din fiecare bloc, ci exclusiv de structura CFG-ului,
     * este mai avantajos pentru această analiză să supradefinim direct funcția de transfer la nivelul unui bloc,
     * care nu face decât să propage informația de la intrarea blocului spre ieșirea acestuia.
     *
     * @param block             blocul prin care se realizează transferul informației
     * @param inInfo            informația la intrarea în bloc
     * @param orderInstructions funcție de ordonare a instrucțiunilor din bloc, în funcție de tipul analizei
     *                          (forward/backwards)
     * @return                  informația la ieșirea din bloc, identică cu cea de la intrare
     */
    @Override
    protected List<BasicBlock> transfer(
            BasicBlock block,
            List<BasicBlock> inInfo,
            UnaryOperator<List<Instruction>> orderInstructions
    ) {
        return inInfo;
    }

    /**
     * Funcția de agregare a informației de la ieșirea predecesorilor consideră că un bloc domină imediat blocul curent
     * dacă inițiază sufixul comun al căilor din arborele de dominanță de la predecesorii nodului curent către rădăcină.
     * Funcția depune în accumInfo acel nod, ca unic element.
     *
     * @param accumInfo acumulatorul modificabil, care a agregat informația unei părți a predecesorilor
     * @param predInfo  informația următorului predecesor
     */
    @Override
    protected void merge(List<BasicBlock> accumInfo, List<BasicBlock> predInfo) {
        // Dacă acumulatorul conține un dominator imediat candidat, iar predecesorul curent a fost procesat, simulăm
        // intersecția mulțimilor de dominatori codificate de dominatorul imediat candidat și de predecesorul curent,
        // depunând rezultatul înapoi în acumulator.
        if (! accumInfo.isEmpty() && ! predInfo.isEmpty()) {
            var result = intersect(accumInfo.getFirst(), predInfo.getFirst());
            accumInfo.set(0, result);
        }
        // Altfel, cel mult unul dintre acumulator și predecesor conțin informație utilă, pe care o stocăm în noul
        // acumulator.
        else {
            // Instrucțiunea aceasta ne ajută să nu mai verificăm care dintre accumInfo și/sau predInfo sunt vide.
            accumInfo.addAll(predInfo);
            // Păstrăm doar primul element
            accumInfo.subList(1, accumInfo.size()).clear();
        }
    }

    /**
     * Simulează intersecția a două mulțimi de dominatori, codificate prin dominatorii imediați. Implementează
     * algoritmul de urcare în arborele de dominanță prezentat în descrierea clasei, bazat pe numerele de post-ordine
     * inversă.
     *
     * @param  block1 reprezentantul primei mulțimi
     * @param  block2 reprezentantul celei de-a doua mulțimi
     * @return        reprezentantul intersecției celor două mulțimi, care inițiază sufixul comun al căilor ascendente
     *                din arborele de dominanță, de la cei doi reprezentanți către rădăcină
     */
    protected BasicBlock intersect(BasicBlock block1, BasicBlock block2) {
        // Căutarea continuă până la întâlnirea unui strămoș comun. Dominatorii imediați se află pe ultima poziție
        // din listele cu rol de adnotare. Pe prima poziție se găsește blocul adnotat însuși.
        while (block1 != block2) {
            while (postOrderNumbering.getReversePostOrderNumber(block1)
                    > postOrderNumbering.getReversePostOrderNumber(block2))
                block1 = blockInInfos.get(block1).getLast();

            while (postOrderNumbering.getReversePostOrderNumber(block2)
                    > postOrderNumbering.getReversePostOrderNumber(block1))
                block2 = blockInInfos.get(block2).getLast();
        }

        return block1;
    }

    /**
     * După agregarea informației de la ieșirea predecesorilor, care produce dominatorul imediat candidat al blocului
     * curent, funcția adaugă pe prima poziție blocul curent însuși, pentru reconstituire formei aștepte a listei:
     * [predecesor, dominator imediat al predecesorului].
     *
     * @param block      blocul curent
     * @param mergedInfo informația agregată a tuturor predecesorilor
     */
    @Override
    protected void finishInfo(BasicBlock block, List<BasicBlock> mergedInfo) {
        // Adăugăm pe prima poziție blocul curent, în fața dominatorului imediat asociat
        mergedInfo.addFirst(block);
    }

    @Override
    protected String infoToString(List<BasicBlock> info) {
        return info.getLast().getDisplayName();
    }
}
