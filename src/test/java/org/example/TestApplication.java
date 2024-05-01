package org.example;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestApplication.class);

  @Bean(destroyMethod = "close")
  public RestHighLevelClient restHighLevelClient(
      @Value("${spring.elasticsearch.host}")
      String host,

      @Value("${spring.elasticsearch.port}")
      Integer port,

      @Value("${spring.elasticsearch.username}")
      String userName,

      @Value("${spring.elasticsearch.password}")
      String password) {

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

    RestClientBuilder builder =
        RestClient.builder(new HttpHost(host, port))
            .setHttpClientConfigCallback(
                httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

    RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
    try {
      restHighLevelClient.ping(RequestOptions.DEFAULT);
    } catch (Exception e) {
      LOGGER.error("Error while connecting to elastic search", e);
      System.exit(1);
    }
    return restHighLevelClient;
  }

}
