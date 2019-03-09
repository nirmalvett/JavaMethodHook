package com.vettiankal;

public class TestMethodHook {

    private long time;

    public void start(Object... args) {
        time = System.currentTimeMillis();
    }

    public void stop(Object... args) {
        System.out.println(System.currentTimeMillis() - time);
    }

}
