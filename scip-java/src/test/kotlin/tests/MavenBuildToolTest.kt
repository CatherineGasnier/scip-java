package tests

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class MavenBuildToolTest : BuildToolHarness() {

    @TestFactory
    fun tests(): List<DynamicTest> =
        listOf(
            checkBuild(
                "basic",
                fixture = "maven/basic",
                expectedScipFiles = 2,
                expectedPackages =
                    """
                    |maven:junit:junit:4.13.1
                    |maven:org.hamcrest:hamcrest-core:1.3
                    |maven:org.scip-code:example:1.0-SNAPSHOT
                    |"""
                        .trimMargin(),
                // The testCompile goal writes to Maven's test output directory.
                expectedTestCode =
                    """
                    |src/main/java/com/Example.java: not test code
                    |src/test/java/com/ExampleSuite.java: test code
                    |"""
                        .trimMargin(),
            ),
            checkBuild(
                "build-command",
                fixture = "maven/basic",
                expectedScipFiles = 1,
                extraArguments = listOf("--", "compile"),
            ),
        )
}
