package org.scip_code.scip_java.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.scip_code.scip.Document;
import org.scip_code.scip.Occurrence;
import org.scip_code.scip.SymbolRole;

class ScipDocumentBuilderTest {

  @Test
  void testCodeAddsTheTestRoleToEveryOccurrence() {
    ScipDocumentBuilder builder = new ScipDocumentBuilder(true);
    addDefinitionAndReference(builder);

    assertEquals(
        List.of(
            SymbolRole.Definition_VALUE | SymbolRole.Test_VALUE, // definition
            SymbolRole.Test_VALUE), // reference
        roles(builder.build("java", "src/test/java/ExampleTest.java", "")));
  }

  @Test
  void otherCodeKeepsTheOccurrenceRoles() {
    ScipDocumentBuilder builder = new ScipDocumentBuilder(false);
    addDefinitionAndReference(builder);

    assertEquals(
        List.of(SymbolRole.Definition_VALUE, 0),
        roles(builder.build("java", "src/main/java/Example.java", "")));
  }

  private static void addDefinitionAndReference(ScipDocumentBuilder builder) {
    builder.addOccurrence(occurrence(0, "example/Example#", SymbolRole.Definition_VALUE));
    builder.addOccurrence(occurrence(1, "java/lang/String#", 0));
  }

  private static Occurrence occurrence(int line, String symbol, int roles) {
    return Occurrence.newBuilder()
        .setSymbol(symbol)
        .setSymbolRoles(roles)
        .setSingleLineRange(ScipRange.singleLine(line, 0, 1).toSingleLineRange())
        .build();
  }

  private static List<Integer> roles(Document document) {
    return document.getOccurrencesList().stream()
        .map(Occurrence::getSymbolRoles)
        .collect(Collectors.toList());
  }
}
