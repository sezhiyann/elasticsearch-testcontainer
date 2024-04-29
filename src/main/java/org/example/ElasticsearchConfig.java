package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticsearchConfig {


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
      log.error("Error while connecting to elastic search", e);
      System.exit(1);
    }
    return restHighLevelClient;
  }
}
