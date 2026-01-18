package irgen;

import analysis.DominatorTree;
import semantic.IdSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic block, care constituie un nod al CFG-ului. Include o listă ordonată de instrucțiuni.
 *
 * Fiecare basic block este caracterizat de eticheta sa la care se pot realiza salturi, motiv pentru care extinde
 * IdSymbol.
 */
public class BasicBlock extends IdSymbol {
    /**
     * Lista ordonată de instrucțiuni
     */
    public List<Instruction> instructions = new ArrayList<>();

    /**
     * Lista ordonată de succesori ai blocului curent.
     */
    public List<BasicBlock> successors = new ArrayList<>();

    /**
     * Lista ordonată de predecesori ai blocului curent.
     */
    public List<BasicBlock> predecessors = new ArrayList<>();

    /**
     * Toate blocurile sunt etichetate
     *
     * @param name eticheta
     */
    public BasicBlock(String name) {
        super(name);
        applyNextVersion();
    }

    /**
     * Adaugă o nouă instrucțiune la acest bloc, care la rândul său este marcat drept bloc conținător pentru
     * instrucțiune. Instrucțiunile PHI (la transformarea în forma SSA) sunt adăugate la începutul blocului. Toate
     * celelalte instrucțiuni sunt adăugate la final. Dacă instrucțiunea este de salt (BR), se actualizează
     * succesorii blocului curent, pe baza operanzilor instrucțiunii de salt, iar blocul curent devine predecesor
     * pentru toți succesorii lui.
     *
     * @param instruction instrucțiunea de adăugat
     */
    public void addInstruction(Instruction instruction) {
        // Blocul curent conține instrucțiunea
        instruction.containingBlock = this;

        // Instrucțiunile PHI sunt adăugate la începutul blocului
        if (instruction.iType == InstructionType.PHI) {
            instructions.addFirst(instruction);
            return;
        }

        // Toate celelalte instrucțiuni sunt adăugate la final
        instructions.add(instruction);

        // În cazul unei instrucțiuni de salt, se actualizează lista de succesori.
        if (instruction.iType == InstructionType.BR) {
            // condiționat: BR condiție bloc1 bloc2
            if (instruction.operands.size() == 3) {
                addSuccessor((BasicBlock) instruction.operands.get(1));
                addSuccessor((BasicBlock) instruction.operands.get(2));
            }
            // necondiționat: BR bloc0
            else
                addSuccessor((BasicBlock) instruction.operands.get(0));
        }
    }

    /**
     * Asigură legăturile bidirecționale dintre blocuri: blocul curent capătă un succesor, iar succesorul, blocul curent
     * ca predecesor.
     *
     * @param successor succesorul de adăugat
     */
    public void addSuccessor(BasicBlock successor) {
        successors.add(successor);
        successor.predecessors.add(this);
    }

    /**
     * Adaugă legături bidirecționale între blocuri, similar funcției de mai sus. În plus, dacă succesorul nou conține
     * instrucțiuni ϕ, le îmbogățește operanzii cu perechi de operanzi valoare-bloc, în care blocul este cel curent,
     * iar valoarea este preluată de la blocul primit ca al doilea parametru, care se presupune că apare deja
     * în instrucțiunea ϕ.
     *
     * De exemplu, dacă se invocă block.addSuccessor(succ, block0), iar succ conținea deja instrucțiunea
     * ... = ϕ v0 v1 block0 block1, noua formă a instrucțiunii este ... = ϕ v0 v1 v0 block0 block1 block. Se observă
     * că valoarea corespunzătoare lui block, v0, a fost preluată de la block0.
     *
     * @param successor         succesorul de adăugat
     * @param intermediateBlock blocul de la care se preia operandul valoare al instrucțiunilor ϕ din succesor, aferent
     *                          blocului curent, cu rol de predecesor
     */
    public void addSuccessor(BasicBlock successor, BasicBlock intermediateBlock) {
        // Adăugăm succesorul în lista de succesori ai blocului curent în stânga blocului intermediar. Această abordare
        // conservă ordinea vizuală a blocurilor.
        var intermediateBlockIndex = successors.indexOf(intermediateBlock);
        successors.add(intermediateBlockIndex, successor);

        // Adăugăm blocul curent în lista de predecesori ai succesorului în stânga blocului intermediar
        intermediateBlockIndex = successor.predecessors.indexOf(intermediateBlock);
        successor.predecessors.add(intermediateBlockIndex, this);

        // Tratăm instrucțiunile ϕ din succesor
        for (var instruction : successor.instructions)
            if (instruction.iType == InstructionType.PHI) {
                instruction.operands = new ArrayList<>(instruction.operands);

                // Sanity check: predecesorii apar în listă în aceeași ordine în care apar ca operanzi
                // ai instrucțiunilor ϕ
                if (intermediateBlockIndex + instruction.operands.size() / 2 != instruction.operands.indexOf(intermediateBlock))
                    throw new IllegalStateException();

                // Determinăm valoarea preluată de la blocul intermediar
                intermediateBlockIndex = instruction.operands.indexOf(intermediateBlock);
                var intermediateValueIndex = instruction.getPhiValueIndex(intermediateBlockIndex);
                var intermediateValue = instruction.operands.get(intermediateValueIndex);

                // Dacă valoarea preluată de la blocul intermediar este definită în blocul intermediar însuși
                if (intermediateValue instanceof Instruction intermediateInstruction
                        && intermediateInstruction.containingBlock == intermediateBlock) {
                    // Dacă instrucțiunea de definire este ϕ, căutăm în aceasta valoarea preluată de la blocul curent
                    if (intermediateInstruction.iType == InstructionType.PHI) {
                        var predecessorIndex = intermediateInstruction.operands.indexOf(this);
                        intermediateValueIndex = intermediateInstruction.getPhiValueIndex(predecessorIndex);
                        intermediateValue = intermediateInstruction.operands.get(intermediateValueIndex);
                    }
                    // Dacă instrucțiunea de definire este de copiere (în care poate degenera o instrucțiune ϕ după
                    // eliminarea unor predecesori cu excepția unuia), preluăm operandul unic
                    else if (intermediateInstruction.iType == InstructionType.COPY)
                        intermediateValue = intermediateInstruction.operands.getFirst();
                }

                // Adăugăm operanzii valoare, respectiv bloc. Utilizând poziția blocului sursă ca reper pentru adăugare,
                // se menține coerența vizuală a predecesorilor din graf și a operanzilor instrucțiunilor ϕ.
                instruction.operands.add(intermediateBlockIndex, this);
                instruction.operands.add(intermediateValueIndex, intermediateValue);
            }
    }

    /**
     * Înlătură legăturile bidirecționale dintre blocuri și elimină din instrucțiunile phi din succesor operanzii
     * corespunzători blocului curent. Dacă succesorul rămâne cu un singur predecesor, instrucțiunea ϕ este înlocuită
     * cu una de copiere a valorii rămase.
     *
     * @param successor succesorul de înlăturat
     */
    public void removeSuccessor(BasicBlock successor) {
        if (! successors.remove(successor))
            throw new IllegalArgumentException("Block does not have specified successor");

        successor.predecessors.remove(this);

        // Tratăm instrucțiunile ϕ din succesor
        for (var instruction : successor.instructions)
            if (instruction.iType == InstructionType.PHI) {
                // Dacă rămâne un singur predecesor al succesorului, înlocuim instrucțiunea ϕ cu una de copiere
                // dinspre unica valoare rămasă
                if (successor.predecessors.size() == 1) {
                    // Determinăm indecșii operanzilor bloc și valoare aferenți unicului predecesor rămas
                    var predecessorIndex = instruction.operands.indexOf(successor.predecessors.getFirst());
                    var valueIndex = instruction.getPhiValueIndex(predecessorIndex);

                    // Rescriem instrucțiunea
                    instruction.iType = InstructionType.COPY;
                    instruction.operands = Collections.singletonList(instruction.operands.get(valueIndex));
                }
                // Dacă rămân mai mulți predecesori ai succesorului, înlăturăm operanzii bloc și valoare aferenți
                // blocului curent
                else {
                    // Determinăm indecșii operanzilor bloc și valoare aferenți blocului curent
                    var predecessorIndex = instruction.operands.indexOf(this);
                    var valueIndex = instruction.getPhiValueIndex(predecessorIndex);

                    // Eliminăm operanzii, mai întâi pe cel bloc, aflat la un index mai mare. Dacă înlăturăm mai întâi
                    // operandul valoare, aflat la index mai mic, indexul mai mare aferent blocului nu mai este valid.
                    instruction.operands = new ArrayList<>(instruction.operands);
                    instruction.operands.remove(predecessorIndex);
                    instruction.operands.remove(valueIndex);
                }
            }
    }

    /**
     * Înlocuiește un succesor cu altul, actualizând în consecință instrucțiunea de salt din blocul curent
     * și instrucțiunile ϕ din vechiul și noul succesor.
     *
     * @param oldSuccessor vechiul succesor
     * @param newSuccessor noul succesor
     */
    public void replaceSuccessor(BasicBlock oldSuccessor, BasicBlock newSuccessor) {
        instructions.getLast().operands = new ArrayList<>(instructions.getLast().operands);
        Collections.replaceAll(instructions.getLast().operands, oldSuccessor, newSuccessor);
        addSuccessor(newSuccessor, oldSuccessor);
        removeSuccessor(oldSuccessor);
    }

    /**
     * Stabilește un unic succesor al blocului curent, care obligatoriu trebuie să se încheie cu un salt. Elimină
     * ceilalți succesori, și blocul curent ca predecesor ai succesorilor eliminați. În cazul în care succesorul primit
     * ca parametru nu se afla deja printre cei existenți, este adăugat acum.
     *
     * @param uniqueSuccessor noul unic succesor al blocului curent
     */
    public void setUniqueSuccessor(BasicBlock uniqueSuccessor) {
        // Ultima instrucțiune din bloc trebuie să fie de salt
        var branch = instructions.getLast();

        if (! branch.isBranch())
            throw new IllegalStateException("Block does not end in a branch");

        // Actualizăm operanzii instrucțiunii de salt pentru a indica unicul succesor
        branch.operands = Collections.singletonList(uniqueSuccessor);

        // Parcurgem succesorii blocului curent și îi eliminăm pe cei diferiți de unicul succesor păstrat.
        // toList() copiază lista succesorilor care vor fi eliminați, pentru a evita generarea unei excepții
        // de modificare concurentă cu iterarea.
        successors.stream()
                .filter(successor -> successor != uniqueSuccessor)
                .toList()
                .forEach(this::removeSuccessor);

        // Dacă lista de succesori devine vidă, înseamnă că unicul succesor nu se găsea printre cei vechi, și trebuie
        // adăugat acum
        if (successors.isEmpty())
            addSuccessor(uniqueSuccessor);
    }

    /**
     * Verifică dacă blocul curent este gol din punct de vedere logic, i.e dacă conține doar un salt necondiționat
     * și eventuale instrucțiuni ϕ.
     *
     * @return true, dacă blocul este logic gol
     */
    public boolean isLogicallyEmpty() {
        return instructions.stream().allMatch(instruction ->
                instruction.iType == InstructionType.PHI || instruction.isUnconditionalBranch());
    }

    /**
     * Verifică dacă blocul curent este citit de vreo instrucțiune ϕ din vreun succesor.
     *
     * @return true, dacă proprietatea este satisfăcută
     */
    public boolean isPhiSource() {
        return successors.stream().anyMatch(successor ->
                successor.instructions.stream().anyMatch(instruction ->
                        instruction.iType == InstructionType.PHI && instruction.operands.contains(this)));
    }

    /**
     * Verifică dacă un bloc își postdomină toți predecesorii, pornind de la arborele de postdominanță
     *
     * @param  postDominatorTree arborele de postdominanță
     * @return                   true, dacă proprietatea este satisfăcută
     */
    public boolean postDominatesPredecessors(DominatorTree postDominatorTree) {
        return predecessors.stream()
                .allMatch(predecessor -> postDominatorTree.immediateDominators.get(predecessor) == this);
    }

    @Override
    public String toString() {
        return name + ":\n\t" + instructions.stream()
                .map(Instruction::toString)
                .collect(Collectors.joining("\n\t"));
    }
}
