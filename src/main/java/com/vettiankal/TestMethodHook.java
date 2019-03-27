package com.vettiankal;

/**
 * Example hook implementation, a hook must use the default constructor and implement the methods:
 * start(String classPath, String methodName, Object... args)
 * stop(Object ret)
 *
 * A hook instance will be created for each method call that has the hook enabled, start will be called at the beginning
 * of the method with where it is calling from and its parameters, and stop will be called right before any return
 * statements with the value of what it will return
 */
public class TestMethodHook {

    private long time;
    private String classPath;
    private String methodName;

    /**
     * This starts the hook, is called at the beginning of each hook-enabled method call
     * @param classPath class that is calling the hook
     * @param methodName method name that is calling the hook
     * @param args parameters passed to the method call, an instance of the calling class will be at args[0]
     *             if the method is non-static
     */
    //TODO include method access as param to distinguish between static and non-static methods
    //TODO include desc as well to distinguish between overloaded methods
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

    /**
     * This stops the hook, is called at the end of each hook-enabled method call
     * @param ret The return value of the method, will be null if the return is null OR the method is void type
     */
    public void stop(Object ret) {
        System.out.println(classPath + "::" + methodName + " returned " + (ret == null ? "null" : ret.toString()) + " in "
                + (System.currentTimeMillis() - time) + "ms");
    }

}
