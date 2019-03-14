package com.vettiankal;

public enum Opcodes {

    AALOAD(0x32),
    AASTORE(0x53),
    ACONST_NULL(0x01),
    ALOAD(0x19, 1),
    ALOAD_0(0x2A),
    ALOAD_1(0x2B),
    ALOAD_2(0x2C),
    ALOAD_3(0x2D),
    ANEWARRAY(0xBD, 2),
    ARETURN(0xB0),
    ARRAYLENGTH(0xBE),
    ASTORE(0x3A, 1),
    ASTORE_0(0x4B),
    ASTORE_1(0x4C),
    ASTORE_2(0x4D),
    ASTORE_3(0x4E),
    ATHROW(0xBF),

    BALOAD(0x33),
    BASTORE(0x54),
    BIPUSH(0x10, 1),

    CALOAD(0x34),
    CASTORE(0x55),
    CHECKCAST(0xC0, 2),

    D2F(0x90),
    D2I(0x8E),
    D2L(0x8F),
    DADD(0x63),
    DALOAD(0x31),
    DASTORE(0x52),
    DCMPG(0x98),
    DCMPL(0x97),
    DCONST_0(0x0E),
    DCONST_1(0x0F),
    DDIV(0x6F),
    DLOAD(0x18, 1),
    DLOAD_0(0x26, 1),
    DLOAD_1(0x27, 1),
    DLOAD_2(0x28, 1),
    DLOAD_3(0x29, 1),
    DMUL(0x6B),
    DNEG(0x77),
    DREM(0x73),
    DRETURN(0xAF, 0, false, true),
    DSTORE(0x39, 1),
    DSTORE_0(0x47),
    DSTORE_1(0x48),
    DSTORE_2(0x49),
    DSTORE_3(0x4A),
    DSUB(0x67),
    DUP(0x59),
    DUP_X1(0x5A),
    DUP_X2(0x5B),
    DUP2(0x5C),
    DUP2_X1(0x5D),
    DUP2_X2(0x5E),

    F2D(0x8D),
    F2I(0x8B),
    F2L(0x8C),
    FADD(0x62),
    FALOAD(0x30),
    FASTORE(0x51),
    FCMPG(0x96),
    FCMPL(0x95),
    FCONST_0(0x0B),
    FCONST_1(0x0C),
    FCONST_2(0x0D),
    FDIV(0x6E),
    FLOAD(0x17, 1),
    FLOAD_0(0x22),
    FLOAD_1(0x23),
    FLOAD_2(0x23),
    FLOAD_3(0x25),
    FMUL(0x6A),
    FNEG(0x76),
    FREM(0x72),
    FRETURN(0xAE, 0, false, true),
    FSTORE(0x38, 1),
    FSTORE_0(0x43),
    FSTORE_1(0x44),
    FSTORE_2(0x45),
    FSTORE_3(0x46),
    FSUB(0x66),

    GETFIELD(0xB4, 2),
    GETSTATIC(0xB2, 2),
    GOTO(0xA7, 2, true, false),
    GOTO_W(0xC8, 4, true, false),

    I2B(0x91),
    I2C(0x92),
    I2D(0x87),
    I2F(0x86),
    I2L(0x85),
    I2S(0x93),
    IADD(0x60),
    IALOAD(0x2E),
    IAND(0x7E),
    IASTORE(0x4F),
    ICONST_M1(0x02),
    ICONST_0(0x03),
    ICONST_1(0x04),
    ICONST_2(0x05),
    ICONST_3(0x06),
    ICONST_4(0x07),
    ICONST_5(0x08),
    IDIV(0x6C),
    IF_ACMPEQ(0xA5, 2, true, false),
    IF_ACMPNE(0xA6, 2, true, false),
    IF_ICMPEQ(0x9F, 2, true, false),
    IF_ICMPNE(0xA0, 2, true, false),
    IF_ICMPLT(0xA1, 2, true, false),
    IF_ICMPGE(0xA2, 2, true, false),
    IF_ICMPGT(0xA3, 2, true, false),
    IF_ICMPLE(0xA4, 2, true, false),
    IFEQ(0x99, 2, true, false),
    IFNE(0x9A, 2, true, false),
    IFLT(0x9B, 2, true, false),
    IFGE(0x9C, 2, true, false),
    IFGT(0x9D, 2, true, false),
    IFLE(0x9E, 2, true, false),
    IFNONNULL(0xC7, 2, true, false),
    IFNULL(0xC6, 2, true, false),
    IINC(0x84, 2),
    ILOAD(0x15, 1),
    ILOAD_0(0x1A),
    ILOAD_1(0x1B),
    ILOAD_2(0x1C),
    ILOAD_3(0x1D),
    IMUL(0x68),
    INEG(0x74),
    INSTANCEOF(0xC1, 2),
    INVOKEINTERFACE(0xB9, 4),
    INVOKESPECIAL(0xB7, 2),
    INVOKESTATIC(0xB8, 2),
    INVOKEVIRTUAL(0xB6, 2),
    IOR(0x80),
    IREM(0x70),
    IRETURN(0xAC, 0, false, true),
    ISHL(0x78),
    ISHR(0x7A),
    ISTORE(0x36, 1),
    ISTORE_0(0x3B),
    ISTORE_1(0x3C),
    ISTORE_2(0x3D),
    ISTORE_3(0x3E),
    ISUB(0x64),
    IUSHR(0x7C),
    IXOR(0x82),

    JSR(0xA8, 2, true, false),
    JSR_W(0xC9, 4, true, false),

    L2D(0x8A),
    L2F(0x89),
    L2I(0x88),
    LADD(0x61),
    LALOAD(0x2F),
    LAND(0x7F),
    LASTORE(0x50),
    LCMP(0x94),
    LCONST_0(0x09),
    LCONST_1(0x0A),
    LDC(0x12, 1),
    LDC_W(0x13, 2),
    LDC2_W(0x14, 2),
    LDIV(0x6D),
    LLOAD(0x16, 1),
    LLOAD_0(0x1E),
    LLOAD_1(0x1F),
    LLOAD_2(0x20),
    LLOAD_3(0x21),
    LMUL(0x68),
    LNEG(0x75),
    LOOKUPSWITCH(0xAB, -1), // https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.lookupswitch
    LOR(0x81),
    LREM(0x71),
    LRETURN(0xAD, 0, false, true),
    LSHL(0x79),
    LSHR(0x7B),
    LSTORE(0x37, 1),
    LSTORE_0(0x3F),
    LSTORE_1(0x40),
    LSTORE_2(0x41),
    LSTORE_3(0x42),
    LSUB(0x65),
    LUSHR(0x7D),
    LXOR(0x83),

    MONITORENTER(0xC2),
    MONITOREXIT(0xC3),
    MULTIANEWARRAY(0xC5, 3),

    NEW(0xBB, 2),
    NEWARRAY(0xBC, 1),
    NOP(0x00),

    POP(0x57),
    POP2(0x58),
    PUTFIELD(0xB5, 2),
    PUTSTATIC(0xB3, 2),

    RET(0xA9, 1), // Not a return?
    RETURN(0xB1, 0, false, true),

    SALOAD(0x35),
    SASTORE(0x56),
    SIPUSH(0x11, 2),
    SWAP(0x5F),

    TABLESWITCH(0xAA, -1),

    WIDE(0xC4, -1),

    /*
    BREAKPOINT(0xCA),
    IMPDEP1(0xFE),
    IMPDEP2(0xFF),
    UNUSED(0xBA)
    */

    ;

    private byte code;
    private int args;
    private boolean isJmp;
    private boolean isRet;

    Opcodes(int code) {
        this(code, 0);
    }

    Opcodes(int code, int args) {
        this(code, args, false, false);
    }

    Opcodes(int code, int args, boolean isJmp, boolean isRet) {
        this.code = (byte)code;
        this.args = args;
        this.isJmp = isJmp;
        this.isRet = isRet;
    }

    public byte getCode() {
        return code;
    }

    public int getArgs() {
        return args;
    }

    public boolean isJmp() {
        return isJmp;
    }

    public boolean isRet() {
        return isRet;
    }

    private static final Opcodes[] LOOKUP_TABLE = new Opcodes[255];
    static {
        for(Opcodes opcode : Opcodes.values()) {
            LOOKUP_TABLE[opcode.getCode() & 0xFF] = opcode;
        }
    }

    public static Opcodes getOpcode(byte code) {
        return LOOKUP_TABLE[code & 0xFF];
    }

}
