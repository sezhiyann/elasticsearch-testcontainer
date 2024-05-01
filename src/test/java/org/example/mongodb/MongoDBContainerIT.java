package org.example.mongodb;


import static org.example.mongo.MongoDBContainerExtension.DEFAULT_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import org.example.AbstractIntegrationTest;
import org.example.mongo.MongoDBContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@ClearEnvironmentVariable(key = "TESTCONTAINERS_ENABLED")
class MongoDBContainerIT extends AbstractIntegrationTest {

  public static final String SIMPLE_ENTITY = "simple_entity";
  @Autowired
  MongoTemplate mongoTemplate;

  @Test
  void mongodbRunning() {
    assertTrue(MongoDBContainerExtension.container.isRunning(), "mongodb not started");
    assertNotNull(MongoDBContainerExtension.container.getMappedPort(DEFAULT_PORT));
  }

  @ParameterizedTest(name = "{0}")
  @CsvSource({
      "mongoDataLoadedProperly,6606905a5cf50f20304f4333",
      "commonDataLoaded,6606905a5cf50f20304f4333",
      "classLevelDataLoaded,6606905a5cf50f20304f4850",
      "methodLevelDataLoaded,6606905a5cf50f20304f4111"
  })
  void mongoDataLoadedProperly(String testName, String id) {
    HashMap obj = mongoTemplate.findById(id, HashMap.class,
        SIMPLE_ENTITY);
    assertNotNull(obj);
  }

  @Test
  void nonParameterizedTest() {
    List<HashMap> list = mongoTemplate.find(
        Query.query(Criteria.where("name").is("nonParameterizedTest")),
        HashMap.class,
        SIMPLE_ENTITY);
    assertTrue(list != null && list.size() > 0);
    assertEquals("6606905a5cf50f20304f4678", list.get(0).get("_id").toString());
  }
}
