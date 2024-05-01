package org.example.elasticsearch;

import static org.example.Constant.MONGODB_MEMORY;
import static org.example.PropertyUtil.convertToBytes;
import static org.example.PropertyUtil.isTestContainersEnabled;
import static org.example.PropertyUtil.readIntFromEnvOrProperty;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * JUnit 5 extension to start an elasticsearch container before all tests. example usage:
 * <pre>
 *   {@code @ExtendWith(ElasticsearchContainerExtension.class)}
 */

public class ElasticsearchContainerExtension implements BeforeAllCallback {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
      ElasticsearchContainerExtension.class);
  private static final int DEFAULT_MEMORY = 756;
  public static final int DEFAULT_PORT = 9200;
  public static final org.testcontainers.elasticsearch.ElasticsearchContainer container
      = new org.testcontainers.elasticsearch.ElasticsearchContainer(
      "docker.elastic.co/elasticsearch/elasticsearch:6.8.22")
      .withReuse(true)
      .withPassword("localpwd")
      .withExposedPorts(DEFAULT_PORT, 9300)
      .withClasspathResourceMapping("elasticsearch/jvm.options",
          "/usr/share/elasticsearch/config/jvm.options",
          org.testcontainers.containers.BindMode.READ_ONLY)
      .withClasspathResourceMapping("elasticsearch/elasticsearch.yml",
          "/usr/share/elasticsearch/config/elasticsearch.yml",
          org.testcontainers.containers.BindMode.READ_ONLY)
      .withClasspathResourceMapping("elasticsearch/log4j2.properties",
          "/usr/share/elasticsearch/config/log4j2.properties",
          org.testcontainers.containers.BindMode.READ_ONLY)
      .withLogConsumer(new Slf4jLogConsumer(LOGGER))
      .waitingFor(Wait.forListeningPort());


  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if (isTestContainersEnabled() && !container.isRunning()) {
      LOGGER.info("Starting elastic search container");
      int memory = readIntFromEnvOrProperty(MONGODB_MEMORY, DEFAULT_MEMORY);
      long memoryInBytes = convertToBytes(memory);
      container.withCreateContainerCmdModifier(
          cmd -> cmd.getHostConfig()
              .withMemory(memoryInBytes)
              .withMemorySwap(memoryInBytes));
      container.start();

      System.out.println("\n\n**************** ports **************************\n\n");//NOSONAR
      Integer mappedPort = container.getMappedPort(DEFAULT_PORT);
      System.out.println("Elasticsearch Port : " + mappedPort);//NOSONAR
      System.setProperty("elasticsearch.port", String.valueOf(mappedPort));
      System.setProperty("elasticsearch.hostname", container.getHost());
      System.setProperty("elasticsearch.username", "elastic");
      System.setProperty("elasticsearch.password", "localpwd");
      System.out.println("\n\n****************************************************\n\n");//NOSONAR
    }
  }


}
