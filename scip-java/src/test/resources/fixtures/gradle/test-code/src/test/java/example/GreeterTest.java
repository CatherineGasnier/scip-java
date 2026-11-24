package example;

import org.junit.jupiter.api.Test;

public class GreeterTest {
    @Test
    public void greetsByName() {
        check(new Greeter().greet("Ada"));
    }

    private static void check(String greeting) {
        if (!greeting.equals("Hello, Ada")) throw new AssertionError(greeting);
    }
}
