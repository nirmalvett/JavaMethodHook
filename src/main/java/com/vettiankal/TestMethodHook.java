package com.vettiankal;

import java.util.Arrays;

public class TestMethodHook {

    private long time;

    public void start(String classPath, String methodName, Object... args) {
        System.out.println(Arrays.toString(args));
        time = System.currentTimeMillis();
    }

    public void stop(Object ret) {
        System.out.println(System.currentTimeMillis() - time);
    }

}
