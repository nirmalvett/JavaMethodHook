package com.vettiankal;

public class TestMethodHook {

    private long time;

    public void start(String classPath, String methodName, Object... args) {
        System.out.print(classPath + "::" + methodName + "(");
        for(int i = 0; i < args.length; i++) {
            System.out.print(args[i] == null ? "null" : args[i].toString());
            if(i != args.length - 1) {
                System.out.print(", ");
            } else {
                System.out.println(")");
            }
        }
        time = System.currentTimeMillis();
    }

    public void stop(Object ret) {
        System.out.println(System.currentTimeMillis() - time);
    }

}
