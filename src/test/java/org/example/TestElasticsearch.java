package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestElasticsearch extends AbstractIntegrationTest {

  @Autowired
  private RestHighLevelClient client;

  @Test
  void loadedData() throws IOException {

    BoolQueryBuilder must = QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery("name", "Shirt"));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(must);

    SearchRequest request = new SearchRequest();
    request.source(sourceBuilder);
    request.indices("testindex");
    request.types("_doc");

    SearchResponse response = client.search(request, RequestOptions.DEFAULT);

    assertEquals(1, response.getHits().totalHits);

  }

  @Test
  void test() {
    assertTrue(elasticSearchContainer.isRunning());
    assertNotNull(elasticSearchContainer.getMappedPort(9200));
  }


}
