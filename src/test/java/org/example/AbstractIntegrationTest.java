package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = {
    ElasticSearchDataLoader.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class AbstractIntegrationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationTest.class);

  //add container for elasticsearch
  @Container
  static final GenericContainer elasticSearchContainer
      = new GenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:6.8.22")
      .withExposedPorts(9200, 9300)
      .withEnv("ES_PASSWORD", "localpwd")
      .withClasspathResourceMapping("elasticsearch/jvm.options",
          "/usr/share/elasticsearch/config/jvm.options", BindMode.READ_ONLY)
      .withClasspathResourceMapping("elasticsearch/elasticsearch.yml",
          "/usr/share/elasticsearch/config/elasticsearch.yml", BindMode.READ_ONLY)
      .withClasspathResourceMapping("elasticsearch/log4j2.properties",
          "/usr/share/elasticsearch/config/log4j2.properties", BindMode.READ_ONLY)
      .withCreateContainerCmdModifier(
          cmd -> {
            cmd.getHostConfig()
                .withMemory(756 * 1024 * 1024L)
                .withMemorySwap(756 * 1024 * 1024L);
          })

      .withLogConsumer(new Slf4jLogConsumer(LOGGER))
      .waitingFor(Wait.forListeningPort());


  @DynamicPropertySource
  static void integrationProperties(DynamicPropertyRegistry registry) {
    System.out.println(
        "\n\n************************** ports ****************************************\n\n");
    Integer mappedPort = elasticSearchContainer.getMappedPort(9200);
    System.out.println("Elasticsearch Port : " + mappedPort);
    System.setProperty("elasticsearch.port", String.valueOf(mappedPort));
    System.out.println(
        "\n\n******************************************************************\n\n");
  }
}
