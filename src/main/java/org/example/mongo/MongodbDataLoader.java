package org.example.mongo;


import static org.example.ClasspathResourceUtil.getPath;
import static org.example.PropertyUtil.isTestContainersEnabled;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class MongodbDataLoader implements TestExecutionListener {

  private static ConcurrentHashMap<String, Boolean> executedScripts = new ConcurrentHashMap<>();

  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    if (!testContext.getTestClass().isAnnotationPresent(EnableMongoDBContainer.class)
        && !isTestContainersEnabled()) {
      return;
    }

    executeFile("common_data", "mongodb", "default.js");

    String clazzName = testContext.getTestClass().getName();
    String simpleClazzName = testContext.getTestClass().getSimpleName();
    executeFile(clazzName, "mongodb/" + simpleClazzName, "default.js");
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    if (!testContext.getTestClass().isAnnotationPresent(EnableMongoDBContainer.class)) {
      return;
    }

    String clazzName = testContext.getTestClass().getName();
    String simpleClazzName = testContext.getTestClass().getSimpleName();
    String methodName = testContext.getTestMethod().getName();

    executeFile(clazzName + "." + methodName,
        "mongodb/" + simpleClazzName, methodName + ".js");

  }

  private void executeFile(String scriptKey, String directory, String filePattern)
      throws IOException, InterruptedException {
    if (Boolean.TRUE.equals(executedScripts.getOrDefault(scriptKey, false))) {
      return;
    }

    Optional<Path> singleFilePath = getPath(directory, filePattern);
    if (!singleFilePath.isPresent()) {
      executedScripts.put(scriptKey, true);
      return;
    }

    boolean executed = ExecuteScript.executeScript(MongoDBContainerExtension.container,
        directory + "/" + singleFilePath.get().getFileName());
    executedScripts.put(scriptKey, executed);
  }
}
