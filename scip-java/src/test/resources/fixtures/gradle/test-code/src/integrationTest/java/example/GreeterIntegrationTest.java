package example;

public class GreeterIntegrationTest {
    public String greeting() {
        return new Greeter().greet("integration");
    }
}
