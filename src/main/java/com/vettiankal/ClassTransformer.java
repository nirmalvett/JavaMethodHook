package com.vettiankal;

import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class ClassTransformer extends ClassVisitor implements ClassFileTransformer, Opcodes {

    public static final String PERIOD = "㐀";
    public static final String COMMA = "㐁";
    public static final String IDENTIFIER = "_MT20190309_";

    private List<String> classes;
    private String hookClassPath;
    private String className;
    private boolean def;

    public ClassTransformer(TransformerConfiguration config) {
        super(ASM5);
        this.classes = config.getClasses();
        this.hookClassPath = config.getHook().replace(".", "/");
        this.def = config.isDefaultEnabled();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        this.className = className;
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

        if(className.equals(hookClassPath)) {
            transform = false;
        }

        if(!transform) {
            return null;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        this.cv = cw;
        cr.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        byte[] arr = cw.toByteArray();
        File file = new File(className);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream output = new FileOutputStream(file);
        output.write(arr);
        output.close();
        return arr;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        cv.visitField(ACC_PUBLIC, IDENTIFIER + name + desc, "Z", null, def);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        LocalVariablesSorter sorter = new LocalVariablesSorter(access, desc, mv);
        return mv == null ? null : new MethodTransformer(sorter, className == null ? "null" : className, name, desc, hookClassPath, access);
    }

    /*
        Takes string in the format method(args)
        Eg. methodToVariableString(java.lang.String)
     */
    public static String methodToVariableString(String method) {
        return IDENTIFIER + method.replace(".", PERIOD).replace(",", COMMA);
    }

}
