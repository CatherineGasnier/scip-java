package org.scip_code.scip_java.javac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ScipOptionBuilderTest {

  @Test
  void mavenTestCompilationIsTestCode() {
    assertEquals(
        "\"-Xplugin:scip -sourceroot: -targetroot: -test-code:on\"",
        pluginOption("-d", "/project/target/test-classes", "Example.java"));
  }

  @Test
  void mavenMainCompilationIsNotTestCode() {
    assertEquals(
        "\"-Xplugin:scip -sourceroot: -targetroot:\"",
        pluginOption("-d", "\"/project/target/classes\"", "Example.java"));
  }

  /** The {@code -Xplugin:scip} option that the builder adds to the given javac arguments. */
  private static String pluginOption(String... javacArguments) {
    ScipOptionBuilder builder = new ScipOptionBuilder();
    for (String argument : javacArguments) {
      builder.processArgument(argument);
    }
    List<String> result = builder.finalResult();
    return result.get(result.size() - 1);
  }
}
