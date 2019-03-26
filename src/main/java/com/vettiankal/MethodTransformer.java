package com.vettiankal;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodTransformer extends MethodVisitor implements Opcodes {

    private static Pattern pattern = Pattern.compile("\\[*L[^;]+;|\\[[ZBCSIFDJ]|[ZBCSIFDJ]");

    private String classPath;
    private String name;
    private String desc;
    private String hookClassPath;
    private int access;
    private LocalVariablesSorter sorter;
    private int hookIndex;

    private MethodVisitor bv;

    public MethodTransformer(LocalVariablesSorter visitor, MethodVisitor baseVisitor, String classPath, String name, String desc, String hookClassPath, int access) {
        super(ASM7, visitor);
        this.bv = baseVisitor;
        this.classPath = classPath;
        this.name = name;
        this.desc = desc;
        this.sorter = visitor;
        this.hookClassPath = hookClassPath;
        this.access = access;
    }

    @Override
    public void visitCode() {
        hookIndex = sorter.newLocal(Type.getType("L" + hookClassPath + ";"));
        List<String> params = parseMethodDesc(desc);
        Label ifDisabled = new Label();

        boolean isStatic = Modifier.isStatic(access);
        int maxParams = isStatic ? 127 : 126;
        int size = params.size() > maxParams ? maxParams : params.size(); // Cap parameters pushed to hook at 127
        boolean isInit = name.equals("<init>");

        mv.visitInsn(ACONST_NULL);
        bv.visitVarInsn(ASTORE, hookIndex);
        mv.visitFieldInsn(GETSTATIC, classPath, ClassTransformer.cleanString(ClassTransformer.IDENTIFIER + name + desc), "Z");
        mv.visitJumpInsn(IFEQ, ifDisabled);
        mv.visitTypeInsn(NEW, hookClassPath);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, hookClassPath, "<init>", "()V", false);
        bv.visitVarInsn(ASTORE, hookIndex);
        bv.visitVarInsn(ALOAD, hookIndex);
        mv.visitLdcInsn(classPath);
        mv.visitLdcInsn(name);
        mv.visitIntInsn(BIPUSH, size + (!isStatic ? 1 : 0));
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

        int pushed = 0;
        if(!isStatic && !isInit) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, pushed++);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(AASTORE);
        }

        int paramIndex = isStatic ? 0 : 1;
        for(int i = 0; i < size; i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, pushed++);
            paramIndex = visitParam(mv, params.get(i), paramIndex);
            mv.visitInsn(AASTORE);
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, hookClassPath, "start", "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V", false);

        mv.visitLabel(ifDisabled); // If the hook is not enabled skip to after this
        mv.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if(opcode >= IRETURN && opcode <= RETURN) {
            Label ifDisabled = new Label();

            bv.visitVarInsn(ALOAD, hookIndex);
            mv.visitJumpInsn(IFNULL, ifDisabled);

            switch (opcode) {
                case IRETURN:
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    break;
                case LRETURN:
                    mv.visitInsn(DUP2);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    break;
                case FRETURN:
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                    break;
                case DRETURN:
                    mv.visitInsn(DUP2);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    break;
                case ARETURN:
                    mv.visitInsn(DUP);
                    break;
                case RETURN:
                    //TODO maybe add some constant here as the return to indicate void, to diff between void and null returns
                    mv.visitInsn(ACONST_NULL);
                    break;
            }

            bv.visitVarInsn(ALOAD, hookIndex);
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, hookClassPath, "stop", "(Ljava/lang/Object;)V", false);

            mv.visitLabel(ifDisabled);
        }

        mv.visitInsn(opcode);
    }

    private static int visitParam(MethodVisitor visitor, String param, int paramIndex) {
        switch(param.charAt(0)) {
            case 'Z':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                return paramIndex + 1;
            case 'B':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                return paramIndex + 1;
            case 'C':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                return paramIndex + 1;
            case 'S':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                return paramIndex + 1;
            case 'I':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                return paramIndex + 1;
            case 'F':
                visitor.visitVarInsn(FLOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                return paramIndex + 1;
            case 'D':
                visitor.visitVarInsn(DLOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                return paramIndex + 2;
            case 'J':
                visitor.visitVarInsn(LLOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                return paramIndex + 2;
            case '[':
            case 'L':
                visitor.visitVarInsn(ALOAD, paramIndex);
                return paramIndex + 1;
            default:
                throw new IllegalArgumentException("Unable to parse parameter: " + param);
        }
    }

    // TODO Type.getArgumentTypes(descriptor);
    private static List<String> parseMethodDesc(String desc) {
        int beginIndex = desc.indexOf('(');
        int endIndex = desc.lastIndexOf(')');
        if(beginIndex == -1 ^ endIndex == -1) {
            throw new IllegalArgumentException("Invalid method descriptor: " + desc);
        }

        String x0;
        if(beginIndex == -1) {
            x0 = desc;
        } else {
            x0 = desc.substring(beginIndex + 1, endIndex);
        }

        Matcher matcher = pattern.matcher(x0);
        ArrayList<String> listMatches = new ArrayList<>();
        while(matcher.find()) {
            listMatches.add(matcher.group());
        }

        return listMatches;
    }

}
