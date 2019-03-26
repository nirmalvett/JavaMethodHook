package com.vettiankal;

public class TestMain {

    public void test0(boolean b, char c, short s, int i, long l, String j, TestMain k, Object[] arr) {
        System.out.println("Test 0 called");
    }

    protected int test1() {
        System.out.println("Test 1 called");
        return 5;
    }

    private String test2() {
        System.out.println("Test 2 called");
        return "test";
    }

    public static void test3() {
        System.out.println("Test 3 called");
    }

    protected static long test4() {
        System.out.println("Test 4 called");
        return 5L;
    }

    private static Object test5() {
        System.out.println("Test 5 called");
        return null;
    }

    public static void main(String... args) throws NoSuchMethodException {
        // Example disabling method hook at run time, same can be done for enabling
        System.out.println(ClassTransformer.setMethodHook(TestMain.class.getMethod("test3"), false));
        TestMain test = new TestMain();
        test.test0(false, 'f', (short)0, 0,0, "", null, new Object[]{});
        test.test1();
        test.test2();
        test3();
        test4();
        test5();
    }

}
