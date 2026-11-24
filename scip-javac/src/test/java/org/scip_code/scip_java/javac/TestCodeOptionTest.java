package org.scip_code.scip_java.javac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.scip_code.scip.Document;
import org.scip_code.scip.Index;
import org.scip_code.scip.Occurrence;
import org.scip_code.scip.SymbolRole;

/**
 * Tests the {@code -test-code:on} option. Like build tools, the test compiles main and test sources
 * in two javac invocations, and passes the option only to the test compilation.
 */
class TestCodeOptionTest {
  private static final String MAIN = "src/main/java/example/Greeter.java";
  private static final String TEST = "src/test/java/example/GreeterTest.java";

  @Test
  void onlyTheTestCompilationGetsTheTestRole(@TempDir Path sourceroot) throws IOException {
    write(
        sourceroot.resolve(MAIN),
        "package example;",
        "public class Greeter {",
        "  public String greet(String name) {",
        "    return \"Hello, \" + name;",
        "  }",
        "}");
    write(
        sourceroot.resolve(TEST),
        "package example;",
        "public class GreeterTest {",
        "  public void greetsByName() {",
        "    check(new Greeter().greet(\"Ada\"));",
        "  }",
        "  private static void check(String greeting) {",
        "    if (greeting.isEmpty()) throw new AssertionError();",
        "  }",
        "}");
    Path targetroot = sourceroot.resolve("targetroot");
    Path mainClasses = sourceroot.resolve("classes");
    compile(sourceroot, targetroot, MAIN, "", mainClasses);
    compile(sourceroot, targetroot, TEST, " -test-code:on", mainClasses);

    Document main = readShard(targetroot, MAIN);
    assertFalse(main.getOccurrencesList().isEmpty());
    assertTrue(
        main.getOccurrencesList().stream().noneMatch(TestCodeOptionTest::hasTestRole),
        () -> "main code must not have the Test role:\n" + main);

    Document test = readShard(targetroot, TEST);
    assertTrue(
        test.getOccurrencesList().stream().allMatch(TestCodeOptionTest::hasTestRole),
        () -> "every occurrence in test code must have the Test role:\n" + test);
    int testDefinition = SymbolRole.Definition_VALUE | SymbolRole.Test_VALUE;
    int testReference = SymbolRole.Test_VALUE;
    // The test method and the helper method keep their Definition role.
    assertEquals(Set.of(testDefinition), roles(test, "example/GreeterTest#greetsByName()."));
    assertEquals(
        Set.of(testDefinition, testReference), roles(test, "example/GreeterTest#check()."));
    // The reference from test code to main code is a test-code reference.
    assertEquals(Set.of(testReference), roles(test, "example/Greeter#greet()."));
  }

  private static boolean hasTestRole(Occurrence occurrence) {
    return (occurrence.getSymbolRoles() & SymbolRole.Test_VALUE) != 0;
  }

  /** The distinct roles of the occurrences of the symbol that ends with {@code symbolSuffix}. */
  private static Set<Integer> roles(Document document, String symbolSuffix) {
    return document.getOccurrencesList().stream()
        .filter(occurrence -> occurrence.getSymbol().endsWith(symbolSuffix))
        .map(Occurrence::getSymbolRoles)
        .collect(Collectors.toSet());
  }

  /**
   * Compiles one source file with the SCIP plugin, writing classes to {@code classes}, which is
   * also on the classpath.
   */
  private static void compile(
      Path sourceroot, Path targetroot, String source, String extraPluginOptions, Path classes)
      throws IOException {
    Files.createDirectories(classes);
    List<String> arguments = new ArrayList<>();
    arguments.add("-processorpath");
    arguments.add(TestCompiler.PROCESSOR_PATH);
    arguments.add("-classpath");
    arguments.add(classes.toString());
    arguments.add("-d");
    arguments.add(classes.toString());
    arguments.add(
        "-Xplugin:scip -sourceroot:"
            + sourceroot
            + " -targetroot:"
            + targetroot
            + extraPluginOptions);
    arguments.add(sourceroot.resolve(source).toString());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    int exit =
        ToolProvider.getSystemJavaCompiler()
            .run(null, output, output, arguments.toArray(new String[0]));
    assertEquals(0, exit, () -> output.toString(StandardCharsets.UTF_8));
  }

  private static Document readShard(Path targetroot, String source) throws IOException {
    Path shard = targetroot.resolve("META-INF/scip").resolve(source + ".scip");
    return Index.parseFrom(Files.readAllBytes(shard)).getDocuments(0);
  }

  private static void write(Path file, String... lines) throws IOException {
    Files.createDirectories(file.getParent());
    Files.write(file, List.of(lines));
  }
}
