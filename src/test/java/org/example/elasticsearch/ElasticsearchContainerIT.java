package org.example.elasticsearch;


import static org.example.elasticsearch.ElasticsearchContainerExtension.DEFAULT_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;

@ClearEnvironmentVariable(key = "TESTCONTAINERS_ENABLED")
class ElasticsearchContainerIT extends AbstractIntegrationTest {

  @Autowired
  private RestHighLevelClient client;

  @Test
  void elasticsearchRunning() throws IOException {
    assertTrue(ElasticsearchContainerExtension.container.isRunning(), "elasticsearch not started");
    assertNotNull(ElasticsearchContainerExtension.container.getMappedPort(DEFAULT_PORT));
    validateDataCreated("name", "Dhoti");
  }

  @Test
  void classLevelDataLoaded() throws IOException {
    validateDataCreated("name", "Pant");
  }

  @Test
  void methodLevelDataLoaded() throws IOException {
    validateDataCreated("name", "Shirt");
  }

  private void validateDataCreated(String key, String value) throws IOException {
    BoolQueryBuilder must = QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery(key, value));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(must);

    SearchRequest request = new SearchRequest();
    request.source(sourceBuilder);
    request.indices("testindex");
    request.types("testindextype");

    SearchResponse response = client.search(request, RequestOptions.DEFAULT);

    assertEquals(200, response.status().getStatus());
    assertEquals(1, response.getHits().totalHits);
    List<Map<String, Object>> collect = Arrays.stream(response.getHits().getHits())
        .map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());
    assertEquals(value, collect.get(0).get(key));
  }
}
