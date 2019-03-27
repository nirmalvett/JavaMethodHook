### Usage:
java -javaagent:*\<path to method timer jar>* -jar *\<path to jar to run>*

### Example:
Following example is what is currently in the code, test3() hook is disabled.
```
C:\Users\nirmal\IdeaProjects\MethodTimer\target>java -javaagent:MethodTimer.jar -jar MethodTimer.jar
Added class transformer
com/vettiankal/TestMain::main([Ljava.lang.String;@5e91993f)
true
com/vettiankal/TestMain::<init>(null)
com/vettiankal/TestMain::<init> returned null in 0ms
com/vettiankal/TestMain::test0(com.vettiankal.TestMain@1c4af82c, false, f, 0, 0, 0, , null, [Ljava.lang.Object;@379619aa)
Test 0 called
com/vettiankal/TestMain::test0 returned null in 1ms
com/vettiankal/TestMain::test1(com.vettiankal.TestMain@1c4af82c)
Test 1 called
com/vettiankal/TestMain::test1 returned 5 in 0ms
com/vettiankal/TestMain::test2(com.vettiankal.TestMain@1c4af82c)
Test 2 called
com/vettiankal/TestMain::test2 returned test in 2ms
Test 3 called
com/vettiankal/TestMain::test4()
Test 4 called
com/vettiankal/TestMain::test4 returned 5 in 1ms
com/vettiankal/TestMain::test5()
Test 5 called
com/vettiankal/TestMain::test5 returned null in 0ms
com/vettiankal/TestMain::main returned null in 14ms
```