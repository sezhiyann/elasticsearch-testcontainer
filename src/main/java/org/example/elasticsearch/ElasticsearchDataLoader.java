package org.example.elasticsearch;

import static org.example.ClasspathResourceUtil.getPaths;
import static org.example.PropertyUtil.isTestContainersEnabled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class ElasticsearchDataLoader implements TestExecutionListener {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
      ElasticsearchDataLoader.class);
  private static ConcurrentHashMap<String, Boolean> loadedData = new ConcurrentHashMap<>();

  private static ConcurrentHashMap<String, String> indexTypes = new ConcurrentHashMap<>();


  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    if (skipDataLoad(testContext)) {
      return;
    }

    RestHighLevelClient client = testContext.getApplicationContext()
        .getBean(RestHighLevelClient.class);

    loadSettings(client);

    loadData(client, "common_data", "elasticsearch/data", "default_*.json");

    String clazzName = testContext.getTestClass().getName();
    String simpleClazzName = testContext.getTestClass().getSimpleName();
    loadData(client, clazzName, "elasticsearch/data/" + simpleClazzName, "default_*.json");
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    if (skipDataLoad(testContext)) {
      return;
    }

    RestHighLevelClient client = testContext.getApplicationContext()
        .getBean(RestHighLevelClient.class);

    String clazzName = testContext.getTestClass().getName();
    String simpleClazzName = testContext.getTestClass().getSimpleName();
    String methodName = testContext.getTestMethod().getName();
    loadData(client, clazzName + "." + methodName,
        "elasticsearch/data/" + simpleClazzName, methodName + "_*.json");

    //validate why this is needed? es needs few ms to refresh itself, before the assertion happens in test
    Thread.sleep(500);
  }

  private void loadData(RestHighLevelClient client, String key, String directory,
      String filePattern)
      throws IOException {
    if (Boolean.TRUE.equals(loadedData.getOrDefault(key, false))) {
      return;
    }

    getPaths(directory, filePattern)
        .stream()
        .filter(Files::exists)
        .forEach(path -> loadData(client, key, path));
  }

  private void loadData(RestHighLevelClient client, String key, Path path) {
    try {
      String indexTypeStr = path.getFileName().toString().replace(".json", "");
      String index = indexTypeStr.substring(indexTypeStr.lastIndexOf("_") + 1);
      String source = new String(Files.readAllBytes(path));
      IndexRequest indexRequest = new IndexRequest(index);
      indexRequest.type(indexTypes.get(index));
      indexRequest.source(source, XContentType.JSON);
      IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
      if (response.status().getStatus() != 201) {
        throw new Exception(
            "Failed to create data : " + index + " with response : " + response);
      }

      loadedData.put(key, true);
      LOGGER.info("Created data from file : {}", path);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private void loadSettings(RestHighLevelClient client) throws IOException {
    if (Boolean.TRUE.equals(loadedData.getOrDefault("settings", false))) {
      return;
    }

    getPaths("elasticsearch/settings", "*_*.json")
        .stream()
        .filter(Files::exists)
        .forEach(path -> createIndex(client, path));
    loadedData.put("settings", true);
    LOGGER.info("Created indices from all settings located at elasticsearch/settings/*.json");
  }

  private static void createIndex(RestHighLevelClient client, Path path) {
    try {
      String indexTypeStr = path.getFileName().toString().replace(".json", "");
      String index = indexTypeStr.substring(0, indexTypeStr.lastIndexOf("_"));
      String type = indexTypeStr.substring(indexTypeStr.lastIndexOf("_") + 1);
      indexTypes.put(index, type);

      String source = new String(Files.readAllBytes(path));
      CreateIndexRequest request = new CreateIndexRequest(index);
      request.source(source, XContentType.JSON);
      CreateIndexResponse response = client.indices()
          .create(request, RequestOptions.DEFAULT);
      if (!response.isAcknowledged()) {
        throw new Exception(
            "Failed to create index : " + index + " with response : " + response);
      }
      LOGGER.trace("Created settings from file : {}", path);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean skipDataLoad(TestContext testContext) {
    return !testContext.getTestClass().isAnnotationPresent(EnableElasticsearchContainer.class)
        && !isTestContainersEnabled();
  }

}
