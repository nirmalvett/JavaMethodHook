package com.vettiankal;

public class TestMain {

    private static boolean TEST = true;
    private static boolean TEST2 = true;

    public int test0(boolean b, char c, short s, int i, long l, String j, TestMain k, Object[] arr) {
        int one = 1;
        int two = 2;
        switch(i) {
            case 0: return one;
            case 1: return two;
        }

        TestMethodHook testMethodHook = null;
        if(TEST) {
            testMethodHook = new TestMethodHook();
            testMethodHook.start(b, c, s, i, l, j, k, arr);
        }

        System.out.println("Test 0 called");
        if(testMethodHook != null) {
            testMethodHook.stop(null);
            return one;
        }

        return two;
    }

    protected void test1() {
        TestMethodHook testMethodHook = null;
        if(TEST2) {
            testMethodHook = new TestMethodHook();
            testMethodHook.start();
        }

        System.out.println("Test 1 called");
        if(testMethodHook != null) {
            testMethodHook.stop(null);
        }
    }

    private void test2() {
        System.out.println("Test 2 called");
    }

    public static void test3() {
        System.out.println("Test 3 called");
    }

    protected static void test4() {
        System.out.println("Test 4 called");
    }

    private static void test5() {
        System.out.println("Test 5 called");
    }

    public static void main(String... args) {
        TestMain test = new TestMain();
        test.test0(false, 'f', (short)0, 0,0, "", null, new Object[]{});
        test.test1();
        test.test2();
        test3();
        test4();
        test5();
    }

}
