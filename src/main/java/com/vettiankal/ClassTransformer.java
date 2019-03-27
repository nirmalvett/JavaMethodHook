package com.vettiankal;

import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

        return cw.toByteArray();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        cv.visitField(ACC_PUBLIC | ACC_STATIC, cleanString(IDENTIFIER + name + desc), "Z", null, def).visitEnd();
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        LocalVariablesSorter sorter = new LocalVariablesSorter(access, desc, mv);
        return mv == null ? null : new MethodTransformer(sorter, mv, className == null ? "null" : className, name, desc, hookClassPath, access);
    }

    public static boolean setMethodHook(Method method, boolean enabled) {
        Class clazz = method.getDeclaringClass();
        try {
            Field field = clazz.getField(cleanString(IDENTIFIER + method.getName() + getMethodDescriptor(method)));
            field.setBoolean(null, enabled);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
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

    private static String getDescriptorForClass(Class c) {
        if(c.isPrimitive()) {
            if(c == byte.class) return "B";
            if(c == char.class) return "C";
            if(c == double.class) return "D";
            if(c == float.class) return "F";
            if(c == int.class) return "I";
            if(c == long.class) return "J";
            if(c == short.class) return "S";
            if(c == boolean.class) return "Z";
            if(c == void.class) return "V";
            throw new RuntimeException("Unrecognized primitive " + c);
        }

        if(c.isArray()) return c.getName().replace('.', '/');
        return ('L' + c.getName() + ';').replace('.', '/');
    }

    private static String getMethodDescriptor(Method m) {
        StringBuilder desc = new StringBuilder("(");
        for(Class c: m.getParameterTypes()) {
            desc.append(getDescriptorForClass(c));
        }
        desc.append(")");
        return desc.append(getDescriptorForClass(m.getReturnType())).toString();
    }

}
