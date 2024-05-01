package org.example.mongo;


import static org.example.Constant.MONGODB_MEMORY;
import static org.example.PropertyUtil.convertToBytes;
import static org.example.PropertyUtil.isTestContainersEnabled;
import static org.example.PropertyUtil.readIntFromEnvOrProperty;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * JUnit 5 extension to start a MongoDB container before all tests. example usage:
 * <pre>
 *   {@code @ExtendWith(MongoDBContainerExtension.class)}
 *
 *   if you want to keep the container running between tests, you can use the following:
 *   create a file called <user.dir>/.testcontainers.properties and add the following line:
 *   {@code org.testcontainers.containers.EnableElasticsearchContainer.reuse.enable=true}
 */
public class MongoDBContainerExtension implements BeforeAllCallback {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
      MongoDBContainerExtension.class);

  private static final int DEFAULT_MEMORY = 256;
  public static final int DEFAULT_PORT = 27017;
  public static final org.testcontainers.containers.MongoDBContainer container =
      new org.testcontainers.containers.MongoDBContainer("mongo:6.0.12")
          .withReuse(true)
          .withExposedPorts(DEFAULT_PORT)
          .withCommand("--replSet rs0")
          .withClasspathResourceMapping("mongodb", "/mongodb", BindMode.READ_ONLY)
          .withLogConsumer(new Slf4jLogConsumer(LOGGER))
          .waitingFor(Wait.forListeningPort());

  @Override
  public void beforeAll(ExtensionContext context) {
    if (isTestContainersEnabled() && !container.isRunning()) {
      LOGGER.info("Starting MongoDB container");
      int memory = readIntFromEnvOrProperty(MONGODB_MEMORY, DEFAULT_MEMORY);
      long memoryInBytes = convertToBytes(memory);
      container
          .withCreateContainerCmdModifier(
              cmd -> cmd.getHostConfig()
                  .withMemory(memoryInBytes)
                  .withMemorySwap(memoryInBytes)
          );

      container.start();

      System.out.println("\n\n**************** ports **************************\n\n");//NOSONAR
      Integer mappedPort = container.getMappedPort(DEFAULT_PORT);
      System.out.println("MongoDB Port : " + mappedPort);//NOSONAR
      System.setProperty("mongodb.port", String.valueOf(mappedPort));
      System.setProperty("mongodb.database", "test");
      System.setProperty("mongodb.hostname", container.getHost());
      System.out.println("\n\n****************************************************\n\n");//NOSONAR
    }
  }

}
