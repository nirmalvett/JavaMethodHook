package com.vettiankal;

public class TestMain {

    public void test0(boolean b, char c, short s, int i, long l, String j, TestMain k, Object[] arr) {
        System.out.println("Test 0 called");
    }

    protected void test1() {
        System.out.println("Test 1 called");
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
        System.out.println("Started");
        TestMain test = new TestMain();
        test.test0(false, 'f', (short)0, 0,0, "", null, new Object[]{});
        test.test1();
        test.test2();
        test3();
        test4();
        test5();
    }

}
