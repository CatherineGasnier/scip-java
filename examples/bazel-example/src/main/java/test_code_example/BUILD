load("@rules_java//java:defs.bzl", "java_library", "java_test")

# scip-java gives every occurrence in a `testonly` target the SCIP Test role.
# Test rules such as `java_test` are `testonly` by default.

java_library(
    name = "greeter",
    srcs = ["Greeter.java"],
)

# A helper library that only tests may depend on.
java_library(
    name = "assertions",
    testonly = True,
    srcs = ["GreeterAssertions.java"],
    deps = [":greeter"],
)

java_test(
    name = "GreeterTest",
    srcs = ["GreeterTest.java"],
    main_class = "test_code_example.GreeterTest",
    use_testrunner = False,
    deps = [
        ":assertions",
        ":greeter",
    ],
)
