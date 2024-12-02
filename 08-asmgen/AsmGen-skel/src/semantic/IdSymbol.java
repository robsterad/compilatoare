package semantic;

public class IdSymbol extends Symbol {
    // Fiecare identificator posedă un tip.
    protected TypeSymbol type;

    /*
     * TODO 4 & 5: definiți un atribut care să indice dacă:
     * 	simbolul e global
     * 			sau
     *  simbolul e al unui parametru formal sau al unei variabile locale,
     *  și offset-ul față de $fp
     */
    public boolean isGlobal;

    public IdSymbol(String name) {
        super(name);
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    public TypeSymbol getType() {
        return type;
    }
}