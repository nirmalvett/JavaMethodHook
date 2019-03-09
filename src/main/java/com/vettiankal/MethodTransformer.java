package com.vettiankal;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class MethodTransformer implements ClassFileTransformer {

    public static final String PERIOD = "㐀";
    public static final String COMMA = "㐁";
    public static final String SPACE = "㐂";

    private List<String> classes;

    public MethodTransformer(List<String> classes) {
        this.classes = classes;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        boolean transform = false;
        for(String s : classes) {
            if(className.matches(s)) {
                transform = true;
                break;
            }
        }

        if(!transform) {
            return null;
        }

        String[] classPackage = className.split("/");
        String actualClassName = classPackage[classPackage.length - 1] + ".class";
        ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer);
        JavaClass clazz;
        try {
            clazz = new ClassParser(stream, actualClassName).parse();
        } catch (IOException e) {
            System.err.println("Unable to transform class " + className);
            e.printStackTrace();
            return null;
        }

        //TODO class manipulation


        ByteArrayOutputStream newClazz = new ByteArrayOutputStream();
        try {
            clazz.dump(newClazz);
            return newClazz.toByteArray();
        } catch (IOException e) {
            System.err.println("Unable to dump class " + className);
            e.printStackTrace();
            return null;
        }

    }

}
