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
    public static final String SEMICOLON = "㐁";
    public static final String ARRAY_BRACKET = "電";
    public static final String BRACKET = "買";
    public static final String BACKWARDS_BRACKET = "無";
    public static final String SLASH = "車";
    public static final String IDENTIFIER = "_MT20190309_";

    private List<String> classes;
    private String hookClassPath;
    private String className;
    private boolean def;

    public ClassTransformer(TransformerConfiguration config) {
        super(ASM7);
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
        cr.accept(this, ClassReader.SKIP_FRAMES);
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
        cv.visitField(ACC_PUBLIC | ACC_STATIC, cleanString(IDENTIFIER + name + desc), "Z", null, def).visitEnd();
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        LocalVariablesSorter sorter = new LocalVariablesSorter(access, desc, mv);
        return mv == null ? null : new MethodTransformer(sorter, mv, className == null ? "null" : className, name, desc, hookClassPath, access);
    }

    public static String cleanString(String method) {
        return method
                .replace(".", PERIOD)
                .replace(";", SEMICOLON)
                .replace("[", ARRAY_BRACKET)
                .replace("/", SLASH)
                .replace("(", BRACKET)
                .replace(")", BACKWARDS_BRACKET);
    }

}
