package com.vettiankal;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

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
    public static final String SEMICOLON = "㐂";
    public static final String IDENTIFIER = "_MT20190309_";

    private List<String> classes;

    public MethodTransformer(List<String> classes) {
        this.classes = classes;
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

    public byte[] transform0(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IOException {
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
        JavaClass clazz = new ClassParser(stream, actualClassName).parse();

        //TODO class manipulation
        Method[] methods = clazz.getMethods();
        ConstantPool pool = clazz.getConstantPool();

        for(Method method : methods) {
            if(method.isAbstract() || method.isInterface() || method.isNative()) {
                continue;
            }

            // If > 127 arguments are required, can use sipush or load integer onto stack
            // TODO account -1 from object methods
            Type[] arguments = method.getArgumentTypes();
            int paramsLength = arguments.length;
            if(paramsLength > 127) {
                paramsLength = 0;
                System.out.println("INFO: Params length for method " + className + "::" + method.getName() + " exceeds 127, will not push arguments to hook.");
            }

            // https://en.wikibooks.org/wiki/Java_Programming/Byte_Code
            byte[] codeBytes = method.getCode().getCode();

            ByteArrayOutputStream methodCode = new ByteArrayOutputStream();
            methodCode.write(new byte[] {
                    0x01,                                     // [aconst_null], load null onto the stack
                    (byte)0xC4, 0x3A, (byte)0xFF, (byte)0xFF, // [wide, astore, 16bit index] store null @ the following location in local variable
                    (byte)0xB2, 0x00, 0x00,                   // [getstatic, constant pool index] get static variable associated with method
                    (byte)0x99, 0x00, 0x05,                   // [ifeq, large jump shift, amount to jump], if variable is true jump preCode.length - current instruction
                    (byte)0xBB, 0x00, 0x00,                   // [new, constant pool index] create hook object specified by {placeholder} in constant pool
                    0x59,                                     // [dup] duplicate object to stack
                    (byte)0xB7, 0x00, 0x00,                   // [invokespecial, constant pool index] invokes constructor of hook
                    (byte)0xC4, 0x3A, (byte)0xFF, (byte)0xFF, // [wide, astore, 16bit index] store the object to replace null
                    (byte)0xC4, 0x19, (byte)0xFF, (byte)0xFF, // [wide, aload, 16bit index] load object in
                    0x10, (byte)paramsLength,                 // [bipush, array size] push array size onto stack
                    (byte)0xBD, 0x00, 0x00,                   // [anewarray, constant pool index] create array of objects to hold parameters
            });

            int arrayIndex = 0;
            int offset = method.isStatic() ? 0 : 1;
            for(int localVariableIndex = offset; localVariableIndex < paramsLength + offset; localVariableIndex++, arrayIndex++) {
                Type type = arguments[arrayIndex];
                if(type instanceof BasicType) {
                    byte[] tSpecific = bTypeLoad(type, arrayIndex, localVariableIndex);
                    methodCode.write(new byte[]{
                            0x59,                                     // [dup] duplicate array to stack
                            0x10, (byte) arrayIndex,                  // [bipush, array index] push the index to the stack
                            tSpecific[0], tSpecific[1],               // [load, local variable index] local variable on stack
                            tSpecific[2], tSpecific[3], tSpecific[4], // [invokestatic, constant pool index] call static method to box type
                            0x53                                      // [aastore] save value in array
                    });
                } else if(type instanceof ObjectType) {
                    methodCode.write(new byte[]{
                            0x59,                                     // [dup] duplicate array to stack
                            0x10, (byte) arrayIndex,                  // [bipush, array index] push the index to the stack
                            0x19, (byte) localVariableIndex,          // [aload, local variable index] load object onto stack
                            0x53                                      // [aastore] save value in array
                    });
                } else {
                    System.err.println("Encountered unknown type " + type.getClass().toString());
                    throw new RuntimeException("Unexpected type");
                }
            }

            methodCode.write(codeBytes);

            methodCode.write(new byte[] {

            });
        }


        ByteArrayOutputStream newClazz = new ByteArrayOutputStream();
        clazz.dump(newClazz);
        System.out.println("Transformed class " + className);
        return newClazz.toByteArray();
    }

    private static byte[] bTypeLoad(Type type, int arrayIndex, int localVariableIndex) {
        return new byte[]{};
    }

    private static byte[] oTypeLoad(Type type, int arrayIndex, int localVariableIndex) {
        return new byte[]{};
    }

    /*
        Takes string in the format class::method(args)
        Eg. com.vettiankal.MethodTransform::methodToVariableString(java.lang.String)
     */
    public static String methodToVariableString(String method) {
        return IDENTIFIER + method.replace(".", PERIOD).replace(",", COMMA).replace(":", SEMICOLON);
    }

}
