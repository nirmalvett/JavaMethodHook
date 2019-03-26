package com.vettiankal;

public class TestMethodHook {

    private long time;
    private String classPath;
    private String methodName;

    public void start(String classPath, String methodName, Object... args) {
        System.out.print(classPath + "::" + methodName + "(");
        for(int i = 0; i < args.length; i++) {
            System.out.print(args[i] == null ? "null" : args[i].toString());
            if(i != args.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(")");

        time = System.currentTimeMillis();
        this.classPath = classPath;
        this.methodName = methodName;
    }

    public void stop(Object ret) {
        System.out.println(classPath + "::" + methodName + " returned " + (ret == null ? "null" : ret.toString()) + " in "
                + (System.currentTimeMillis() - time) + "ms");
    }

}
