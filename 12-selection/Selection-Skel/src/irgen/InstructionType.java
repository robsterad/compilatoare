package irgen;

public enum InstructionType {
    COPY,
    LOAD,
    ADD,
    SUB,
    MUL,
    DIV,
    NEG,
    CMP_EQ,
    CMP_LE,
    CMP_LT,
    CALL,
    BR,
    RET,
    PHI {
        @Override
        public String toString() {
            return "ϕ";
        }
    }
}
