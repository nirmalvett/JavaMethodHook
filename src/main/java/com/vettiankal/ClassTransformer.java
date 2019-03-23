package com.vettiankal;

import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class ClassTransformer extends ClassVisitor implements ClassFileTransformer, Opcodes {

    public static final String PERIOD = "㐀";
    public static final String COMMA = "㐁";
    public static final String SEMICOLON = "㐂";
    public static final String IDENTIFIER = "_MT20190309_";

    private List<String> classes;
    private String hook;

    public ClassTransformer(List<String> classes, String hook) {
        super(ASM5);
        this.classes = classes;
        this.hook = hook.replace("/", ".");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            return transform0(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (Throwable e) {
            System.err.println("Error transforming class: " + className);
            e.printStackTrace();
            return null;
        }
    }

    private byte[] transform0(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IOException {
        boolean transform = false;
        for(String s : classes) {
            if(className.matches(s)) {
                transform = true;
                break;
            }
        }

        if(className.equals(hook)) {
            transform = false;
        }

        if(!transform) {
            return null;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        this.cv = cw;
        cr.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return mv == null ? null : new MethodTransformer(mv);
    }

    /*
        Takes string in the format class::method(args)
        Eg. com.vettiankal.MethodTransform::methodToVariableString(java.lang.String)
     */
    public static String methodToVariableString(String method) {
        return IDENTIFIER + method.replace(".", PERIOD).replace(",", COMMA).replace(":", SEMICOLON);
    }

}
