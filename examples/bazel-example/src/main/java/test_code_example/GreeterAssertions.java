package test_code_example;

public class GreeterAssertions {
    public static void assertGreets(Greeter greeter, String name) {
        if (!greeter.greet(name).equals("Hello, " + name)) throw new AssertionError(name);
    }
}
