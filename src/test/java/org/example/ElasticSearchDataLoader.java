package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class ElasticSearchDataLoader implements TestExecutionListener {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public void prepareTestInstance(TestContext testContext) throws Exception {

    RestHighLevelClient client = testContext.getApplicationContext()
        .getBean(RestHighLevelClient.class);

//   Load data into ElasticSearch
    ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    Resource[] settingFiles = patternResolver.getResources(
        "classpath*:elasticsearch/index/**/settings.json");
    Arrays.stream(settingFiles).filter(Resource::exists).forEach(resource -> {
      try {
        Path path = Paths.get(resource.getURI());
        String parent = path.getName(path.getNameCount() - 2).toString();
        String source = new String(resource.getInputStream().readAllBytes());
        CreateIndexRequest request = new CreateIndexRequest(parent);
        request.source(source, XContentType.JSON);
        CreateIndexResponse response = client.indices()
            .create(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    Resource[] dataFiles = patternResolver.getResources(
        "classpath*:elasticsearch/index/**/*data.json");
    Arrays.stream(dataFiles).filter(Resource::exists).forEach(resource -> {
      try {
        Path path = Paths.get(resource.getURI());
        String parent = path.getName(path.getNameCount() - 2).toString();
        String source = new String(resource.getInputStream().readAllBytes());
        IndexRequest indexRequest = new IndexRequest(parent);
        indexRequest.type("_doc");
        indexRequest.source(source, XContentType.JSON);
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
  
}
