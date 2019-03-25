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

    public MethodTransformer(LocalVariablesSorter visitor, String classPath, String name, String desc, String hookClassPath, int access) {
        super(ASM5, visitor);
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
        int size = params.size() > 127 ? 127 : params.size(); // Cap parameters pushed to hook at 127
        boolean isStatic = Modifier.isStatic(access);
        boolean isInit = name.equals("<init>");
        if(!isStatic && !isInit && size == 127) {
            size--;
        }

        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ASTORE, hookIndex);
        mv.visitFieldInsn(GETSTATIC, classPath, ClassTransformer.IDENTIFIER + name + desc, "Z");
        mv.visitJumpInsn(IFEQ, ifDisabled);
        mv.visitTypeInsn(NEW, hookClassPath);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, hookClassPath, "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, hookIndex);
        mv.visitVarInsn(ALOAD, hookIndex);
        mv.visitLdcInsn(classPath);
        mv.visitLdcInsn(name);
        mv.visitIntInsn(BIPUSH, size);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

        if(!isStatic && !isInit) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(AASTORE);
        }

        for(int i = 0; i < size; i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i + (isStatic || isInit ? 0 : 1));
            visitParam(mv, params.get(i), i + (isStatic ? 0 : 1));
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

            mv.visitVarInsn(ALOAD, hookIndex);
            mv.visitJumpInsn(IFNULL, ifDisabled);

            switch (opcode) {
                case IRETURN:
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                    break;
                case LRETURN:
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    break;
                case FRETURN:
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                    break;
                case DRETURN:
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    break;
                case ARETURN:
                    mv.visitInsn(DUP);
                    break;
                case RETURN:
                    mv.visitInsn(ACONST_NULL);
                    break;
            }

            mv.visitVarInsn(ALOAD, hookIndex);
            mv.visitInsn(DUP);
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEVIRTUAL, hookClassPath, "stop", "(Ljava/lang/Object;)V", false);

            mv.visitLabel(ifDisabled);
        }

        mv.visitInsn(opcode);
    }

    private static void visitParam(MethodVisitor visitor, String param, int paramIndex) {
        switch(param.charAt(0)) {
            case 'Z':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                break;
            case 'B':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                break;
            case 'C':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                break;
            case 'S':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                break;
            case 'I':
                visitor.visitVarInsn(ILOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            case 'F':
                visitor.visitVarInsn(FLOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                break;
            case 'D':
                visitor.visitVarInsn(DLOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                break;
            case 'J':
                visitor.visitVarInsn(LLOAD, paramIndex);
                visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                break;
            case '[':
            case 'L':
                visitor.visitVarInsn(ALOAD, paramIndex);
                break;
            default:
                throw new IllegalArgumentException("Unable to parse parameter: " + param);
        }
    }

    private static List<String> parseMethodDesc(String desc) {
        int beginIndex = desc.indexOf('(');
        int endIndex = desc.lastIndexOf(')');
        if(beginIndex == -1 ^ endIndex == -1) {
            throw new IllegalArgumentException("Invalid method descriptor: " + desc);
        }

        String x0;
        if(beginIndex == -1) {
            x0 = desc;
        }
        else {
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
