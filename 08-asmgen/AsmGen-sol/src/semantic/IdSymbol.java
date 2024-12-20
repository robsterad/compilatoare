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

    // Offset from frame pointer ($fp) for local vars and parameters
    // Note: only meaningful when isGlobal is false
    protected int offset;

    // Order in the current scope (used to calculate offset)
    protected int order;

    public IdSymbol(String name) {
        super(name);
        this.isGlobal = false;  // default to local
        this.offset = 0;
        this.order = 0;
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    public TypeSymbol getType() {
        return type;
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}