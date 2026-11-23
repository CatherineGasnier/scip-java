package org.scip_code.scip_java.shared;

import java.nio.file.Path;

/** Settings shared between all SCIP-emitting compiler plugins. */
public class ScipOptions {
  public Path sourceroot;
  public Path targetroot;
  public boolean includeText = false;

  /**
   * Whether the compiled sources are test code, as decided by the build tool (for example, a Gradle
   * compile task whose classes a {@code Test} task runs). See {@link
   * ScipDocumentBuilder#ScipDocumentBuilder(boolean)} for the effect on the SCIP output.
   */
  public boolean testCode = false;

  public NoRelativePathMode noRelativePath = NoRelativePathMode.INDEX_ANYWAY;
  public UriScheme uriScheme = UriScheme.DEFAULT;
}
