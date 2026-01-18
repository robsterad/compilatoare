package selection;

import irgen.Instruction;
import semantic.IdSymbol;
import semantic.IntSymbol;
import semantic.Symbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Algoritmul de acoperire care identifică toate acoperirile posibile ale unui arbore de expresie
 */
public class Tiler {
    /**
     * Mulțimea de reguli terminale
     */
    public Set<TerminalRule> terminalRules;

    /**
     * Mulțimea de reguli cu operator
     */
    public Set<OperatorRule> operatorRules;

    /**
     * Tabelul utilizat pentru identificarea tuturor acoperirilor.
     * Fiecărui nod (simbol) îi corespunde o linie din tabel.
     * Fiecărei coloane (interpretare) din acea linie îi corespunde o mulțime de instanțe de reguli.
     * Mulțimile sunt menținute sortate după cost, astfel încât prima instanță aferentă unei interpretări
     * are întotdeauna costul minim pentru acea interpretare.
     */
    public Map<Symbol, Map<StorageClass, Set<Rule.RuleInstance>>> ruleInstances = new IdentityHashMap<>();

    public Tiler(Set<TerminalRule> terminalRules, Set<OperatorRule> operatorRules) {
        this.terminalRules = terminalRules;
        this.operatorRules = operatorRules;
    }

    /**
     * Adaugă o mulțime de instanțe într-o celulă a tabelului. Se evită adăugarea dacă mulțimea este null.
     *
     * @param symbol        nodul din arborele expresiei (linia)
     * @param storageClass  interpretarea (coloana)
     * @param ruleInstances mulțimea de reguli de adăugat
     */
    public void addRuleInstances(
            Symbol symbol,
            StorageClass storageClass,
            Set<? extends Rule.RuleInstance> ruleInstances
    ) {
        // Se evită adăugarea dacă mulțimea este null
        if (ruleInstances == null)
            return;

        // Mulțimile de instanțe ale regulilor sunt sortate crescător după cost, utilizând un TreeSet
        this.ruleInstances
                .computeIfAbsent(symbol, s -> new HashMap<>())
                .computeIfAbsent(storageClass, sc -> new TreeSet<>(Comparator.comparingInt(instance -> instance.cost)))
                .addAll(ruleInstances);
    }

    /**
     * Întoarce mulțimea de instanțe de reguli dintr-o celulă a tabelului sau null dacă celula este goală
     *
     * @param symbol       nodul din arborele expresiei (linia)
     * @param storageClass interpretarea (coloana)
     * @return             mulțimea de reguli din celulă sau null
     */
    public Set<Rule.RuleInstance> getRuleInstances(Symbol symbol, StorageClass storageClass) {
        var row = ruleInstances.get(symbol);

        if (row == null)
            return null;

        return row.get(storageClass);
    }

    /**
     * Încearcă să acopere subarborele de expresie având ca rădăcină simbolul furnizat
     *
     * @param symbol rădăcina subarborelui expresiei
     */
    public void tile(Symbol symbol) {
        // Dacă simbolul corespunde unei frunze, utilizăm regulile terminale
        if (! (symbol instanceof Instruction instruction))
            terminalRules.forEach(rule -> addRuleInstances(symbol, rule.LHS, match(symbol, rule)));
        // Dacă simbolul corespunde unei instrucțiuni, utilizăm regulile cu operator
        else {
            // În prealabil, încercăm să acoperim copiii neacoperiți deja
            for (var operand : instruction.operands)
                if (ruleInstances.get(operand) == null)
                    tile(operand);

            operatorRules.forEach(rule -> addRuleInstances(symbol, rule.LHS, match(symbol, rule)));
        }
    }

    /**
     * Încearcă să potrivească subarborele de expresie având ca rădăcină simbolul furnizat cu o regulă terminală
     *
     * @param symbol       rădăcina subarborelui expresiei
     * @param terminalRule regula terminală
     * @return             o mulțime cu o singură instanță a regulii terminale, dacă potrivirea se realizează cu succes,
     *                     sau null
     */
    public Set<TerminalRule.TerminalRuleInstance> match(Symbol symbol, TerminalRule terminalRule) {
        // TODO Dacă simbolul nu poate fi privit ca un terminal așteptat de partea dreaptă a regulii, întoarcem null.
        //  Utilizați metoda getTerminal de mai jos


        // TODO Construim instanța regulii terminale.
        //  Având în vedere că clasa TerminalRuleInstance este internă nestatică în clasa TerminalRule, folosiți sintaxa
        //  <obiect clasă externă>.new <clasă internă>().
        //  Pentru a obține simbolul aferent părții stângi, utilizați membrul getInstanceLHS al regulii terminale.
        TerminalRule.TerminalRuleInstance instance = null;

        // Întoarcem mulțimea singleton cu instanța. Nu pot existe instanțe multiple pentru același simbol și regulă
        // terminală.
        return Set.of(instance);
    }

    /**
     * Încearcă să potrivească subarborele de expresie având ca rădăcină simbolul furnizat cu o regulă cu operator
     *
     * @param symbol       rădăcina subarborelui expresiei
     * @param operatorRule regula cu operator
     * @return             o mulțime de instanțe ale regulii terminale, dacă potrivirea se realizează cu succes,
     *                     sau null
     */
    public Set<OperatorRule.OperatorRuleInstance> match(Symbol symbol, OperatorRule operatorRule) {
        // TODO Dacă simbolul nu corespunde unei instrucțiuni, nu putem realiza potrivirea cu o regulă cu operator,
        //  așa că întoarcem null
        if (! (symbol instanceof Instruction instruction))
            return null;

        // TODO Dacă operatorul instrucțiunii nu este cel așteptat de regulă, întoarcem null


        // TODO Dacă numerele de operanzi aferenți instrucțiunii și regulii nu coincid, întoarcem null


        // Pe poziția i a listei de mai jos vom introduce mulțimea de instanțe de reguli aferente copilului i
        var operandInstanceSets = new ArrayList<Set<Rule.RuleInstance>>();

        // Fiecare operand al instrucțiunii trebuie să poată fi potrivit de o regulă care produce o interpretare
        // așteptată de regula curentă pentru poziția respectivă
        for (var i = 0; i < operatorRule.RHS.size(); i++) {
            // Operandul curent
            var operand = instruction.operands.get(i);

            // TODO Determinăm mulțimea instanțelor regulilor care acoperă operandul curent.
            //  Utilizați metoda getRuleInstances de mai sus.
            Set<Rule.RuleInstance> operandInstances = null;

            // TODO Dacă pentru operandul curent, mulțimea instanțelor aferente interpretării așteptate este vidă,
            //  regula nu este aplicabilă, așa că întoarcem null


            // TODO Adăugăm mulțimea aferentă operandului curent în lista operandInstanceSets

        }

        // TODO Având la dispoziție lista mulțimilor de instanțe aferente operanzilor, putem construi instanțele
        //  regulii curente calculând produsul cartezian al mulțimilor din listă. Un element al produsului este
        //  o listă care are pe poziția i o instanță de regulă care acoperă operandul i.
        //  Utilizați metoda cartesianProduct de mai jos.

        // TODO Pe baza produsului cartezian, determinați toate instanțele regulii curente.
        //  Având în vedere că clasa OperatorRuleInstance este internă nestatică în clasa OperatorRule, folosiți sintaxa
        //  <obiect clasă externă>.new <clasă internă>().
        return null;
    }

    /**
     * Întoarce terminalul aferent simbolului, sau null dacă simbolul nu corespunde unei frunze
     *
     * @param symbol simbolul
     * @return       terminalul, dacă simbolul corespunde unei frunze, sau null
     */
    static Terminal getTerminal(Symbol symbol) {
        if (symbol instanceof IntSymbol)
            return Terminal.LITERAL;

        // (symbol instanceof IdSymbol) nu este corect întrucât include Instruction, BasicBlock etc.
        if (symbol.getClass() == IdSymbol.class)
            return Terminal.NAME;

        return null;
    }

    /**
     * Calculează produsul cartezian al mulțimilor dintr-o listă ordonată. Observați inversarea straturilor de tip
     * dintre parametru și rezultat.
     *
     * @param sets lista ordonată de mulțimi
     * @return     o mulțime de liste ordonate, fiecare listă având pe poziția i câte un element din mulțimea i
     * @param <T>  tipul elementelor mulțimilor
     */
    static <T> Set<List<T>> cartesianProduct(List<Set<T>> sets) {
        var result = new HashSet<List<T>>();

        if (sets.isEmpty()) {
            result.add(new LinkedList<>());
            return result;
        }

        var firstSet = sets.getFirst();
        var otherSets = sets.subList(1, sets.size());
        var otherSetsProduct = cartesianProduct(otherSets);

        for (var elem : firstSet)
            for (var list : otherSetsProduct) {
                var listCopy = new LinkedList<>(list);
                listCopy.addFirst(elem);
                result.add(listCopy);
            }

        return result;
    }

    @Override
    public String toString() {
        return ruleInstances.entrySet().stream()
                .map(row -> row.getKey().getDisplayName() + "\n"
                        + row.getValue().entrySet().stream()
                                .map(column -> "\t" + column.getKey() + "\n"
                                        + column.getValue().stream()
                                                .map(instance -> "\t\t" + instance)
                                                .collect(Collectors.joining("\n")))
                                .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n"));
    }
}
