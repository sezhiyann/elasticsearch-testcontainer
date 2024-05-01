package org.example.mongo;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.MongoDBContainer;

public class ExecuteScript {

  private ExecuteScript() {
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteScript.class);

  public static boolean executeScript(MongoDBContainer container, String scriptFile)
      throws IOException, InterruptedException {
    ExecResult execResult = container.execInContainer(buildScriptLoadCommand(scriptFile));
    if (execResult.getExitCode() != 0) {
      LOGGER.error("Error executing script stderr: {}   stdout : {}", execResult.getStderr(),
          execResult.getStdout());
      return false;
    } else {
      LOGGER.info("Executed script stderr: {}   stdout : {}", execResult.getStderr(),
          execResult.getStdout());
      return true;
    }
  }

  private static String[] buildScriptLoadCommand(String pathToFile) {
    return new String[]{"sh", "-c", "mongosh --file \"" + pathToFile + "\""};
  }
}
