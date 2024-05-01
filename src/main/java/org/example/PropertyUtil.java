package org.example;

import static org.example.Constant.TESTCONTAINERS_ENABLED;

import org.slf4j.Logger;
import org.springframework.util.StringUtils;

/**
 * Utility class for handling properties. This class provides methods to read properties from
 * environment variables or system properties.
 */
public class PropertyUtil {

  private PropertyUtil() {
  }


  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PropertyUtil.class);

  /**
   * Reads a property from environment variables or system properties. If the property is not found
   * in either, it returns a default value.
   *
   * @param key          The key of the property to read. key = mongodb.memory in
   *                     System.getProperty() key = MONGODB_MEMORY in System.getenv(). Upper case
   *                     and replace '.' with '_'
   * @param defaultValue The default value to return if the property is not found.
   * @return The value of the property as an integer, or the default value if the property is not
   * found.
   */
  public static int readIntFromEnvOrProperty(String key, int defaultValue) {
    String property = System.getProperty(key);
    if (property == null) {
      property = System.getenv(key.toUpperCase().replace(".", "_"));
    }
    return convertToInteger(property, defaultValue);
  }

  /**
   * Reads a property from environment variables or system properties. If the property is not found
   * in either, it returns a default value.
   *
   * @param key          The key of the property to read. key = mongodb.memory in
   *                     System.getProperty() key = MONGODB_MEMORY in System.getenv(). Upper case
   *                     and replace '.' with '_'
   * @param defaultValue The default value to return if the property is not found.
   * @return The value of the property as an boolean, or the default value if the property is not
   * found.
   */
  public static boolean readBooleanFromEnvOrProperty(String key, boolean defaultValue) {
    String property = System.getProperty(key);
    if (property == null) {
      property = System.getenv(key.toUpperCase().replace(".", "_"));
    }
    return convertToBoolean(property, defaultValue);
  }

  /**
   * Converts a string to an integer. If the string cannot be converted, it logs an error and
   * returns 0.
   *
   * @param intAsString The string to convert to an integer.
   * @return The string converted to an integer, or 0 if the string cannot be converted.
   */
  public static int convertToInteger(String intAsString, int defaultValue) {
    try {
      if (StringUtils.hasText(intAsString)) {
        return Integer.parseInt(intAsString);
      }
    } catch (NumberFormatException e) {
      LOGGER.error("Invalid number specified : {}", intAsString);
    }
    return defaultValue;
  }

  public static boolean convertToBoolean(String intAsString, boolean defaultValue) {
    if (StringUtils.hasText(intAsString)) {
      return Boolean.parseBoolean(intAsString);
    } else {
      return defaultValue;
    }
  }

  public static long convertToBytes(int memory) {
    return memory * 1024 * 1024L;
  }

  public static boolean isTestContainersEnabled() {
    return readBooleanFromEnvOrProperty(TESTCONTAINERS_ENABLED, true);
  }

}