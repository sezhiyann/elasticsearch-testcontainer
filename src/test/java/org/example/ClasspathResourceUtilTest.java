package org.example;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ClasspathResourceUtilTest {

  @Test
  void getPath_whenPathExists_returnsOptionalWithPath() throws IOException {
    Optional<Path> result = ClasspathResourceUtil.getPath(
        "elasticsearch/data/ElasticsearchContainerIT", "*.json");
    assertTrue(result.isPresent());
  }

  @Test
  void getPath_whenDirectoryDoesNotExist_returnsEmptyOptional() throws IOException {
    Optional<Path> result = ClasspathResourceUtil.getPath("/non/existing", "*.json");
    assertFalse(result.isPresent());
  }

  @Test
  void getPath_whenPathDoesNotExist_returnsEmptyOptional() throws IOException {
    Optional<Path> result = ClasspathResourceUtil.getPath(
        "elasticsearch/data/ElasticsearchContainerIT", "*.js");
    assertFalse(result.isPresent());
  }

  @Test
  void getPaths_whenLocationPatternIsNotEmpty_returnsListOfPaths() throws IOException {
    List<Path> result = ClasspathResourceUtil.getPaths("mongodb/MongoDBContainerIT", "*.js");
    assertFalse(result.isEmpty());
  }

  @Test
  void getPaths_whenNonExistentDirectory_returnsEmptyList() throws IOException {
    List<Path> result = ClasspathResourceUtil.getPaths("/non/existing", "*.js");
    assertTrue(result.isEmpty());
  }

  @Test
  void getPaths_whenLocationPatternDoesNotMatchAnyPath_returnsEmptyList() throws IOException {
    List<Path> result = ClasspathResourceUtil.getPaths("mongodb/MongoDBContainerIT", "*.json");
    assertTrue(result.isEmpty());
  }
}