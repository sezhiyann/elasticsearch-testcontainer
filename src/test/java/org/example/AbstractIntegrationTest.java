package org.example;

import org.example.elasticsearch.EnableElasticsearchContainer;
import org.example.mongo.EnableMongoDBContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@EnableDataLoaders
@EnableMongoDBContainer
@EnableElasticsearchContainer
public abstract class AbstractIntegrationTest {

}
