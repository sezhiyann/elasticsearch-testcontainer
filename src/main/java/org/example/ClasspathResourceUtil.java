package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ClasspathResourceUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathResourceUtil.class);
  private static ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

  private ClasspathResourceUtil() {
  }

  public static Optional<Path> getPath(String directory, String pathPattern) throws IOException {

    if (!checkIfExists(directory)) {
      LOGGER.trace("Nothing found for path: {}", directory);
      return Optional.empty();
    }

    Resource[] allTestCommonDataResource = patternResolver.getResources(
        directory + "/" + pathPattern);
    if (allTestCommonDataResource.length > 0 && allTestCommonDataResource[0].exists()) {
      return Optional.of(Path.of(allTestCommonDataResource[0].getURI()));
    } else {
      LOGGER.trace("No file found for path: {}/{}", directory, pathPattern);
      return Optional.empty();
    }
  }

  public static List<Path> getPaths(String directory, String locationPattern) throws IOException {
    List<Path> paths = new ArrayList<>();

    if (!checkIfExists(directory)) {
      LOGGER.trace("Nothing found for path: {}", directory);
      return paths;
    }

    Resource[] allTestCommonDataResource = patternResolver.getResources(
        directory + "/" + locationPattern);
    if (allTestCommonDataResource.length > 0) {
      for (Resource resource : allTestCommonDataResource) {
        if (resource.exists()) {
          paths.add(Path.of(resource.getURI()));
        }
      }
    } else {
      LOGGER.trace("No file found for path: {}", locationPattern);
    }
    return paths;
  }

  public static boolean checkIfExists(String pathPattern) {
    Resource directoryResource = patternResolver.getResource(pathPattern);
    return directoryResource.exists();
  }
}