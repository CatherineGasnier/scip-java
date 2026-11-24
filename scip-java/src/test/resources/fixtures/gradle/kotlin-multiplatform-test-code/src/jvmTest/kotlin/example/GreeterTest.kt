package example

class GreeterTest {
    fun greetsByName() = check(Greeter().greet("Ada") == "Hello, Ada")
}
