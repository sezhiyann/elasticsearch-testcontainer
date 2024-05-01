package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.RestoreEnvironmentVariables;
import org.junitpioneer.jupiter.RestoreSystemProperties;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;

@RestoreEnvironmentVariables
@RestoreSystemProperties
class PropertyUtilTest {

  @Test
  @SetEnvironmentVariable(key = "EXISTING_PROPERTY", value = "123")
  void readIntFromEnvOrProperty_whenEnvExists_returnsPropertyValue() {
    int result = PropertyUtil.readIntFromEnvOrProperty("existing.property", 0);
    assertEquals(123, result);
  }

  @Test
  void readIntFromEnvOrProperty_whenPropertyExists_returnsPropertyValue() {
    System.setProperty("existing.property", "123");
    int result = PropertyUtil.readIntFromEnvOrProperty("existing.property", 0);
    assertEquals(123, result);
  }

  @Test
  void readIntFromEnvOrProperty_whenPropertyDoesNotExist_returnsDefaultValue() {
    int result = PropertyUtil.readIntFromEnvOrProperty("non.existing.property", 345);
    assertEquals(345, result);
  }

  @Test
  void readBooleanFromEnvOrProperty_whenPropertyExists_returnsPropertyValue() {
    System.setProperty("existing.property", "true");
    boolean result = PropertyUtil.readBooleanFromEnvOrProperty("existing.property", false);
    assertTrue(result);
  }

  @Test
  void readBooleanFromEnvOrProperty_whenPropertyDoesNotExist_returnsDefaultValue() {
    boolean result = PropertyUtil.readBooleanFromEnvOrProperty("non.existing.property", true);
    assertTrue(result);
  }

  @Test
  void convertToInteger_whenInputIsNumber_returnsConvertedNumber() {
    int result = PropertyUtil.convertToInteger("123", 0);
    assertEquals(123, result);
  }

  @Test
  void convertToInteger_whenInputIsNotNumber_returnsDefaultValue() {
    int result = PropertyUtil.convertToInteger("not a number", 0);
    assertEquals(0, result);
  }

  @Test
  void convertToBoolean_whenInputIsTrue_returnsTrue() {
    boolean result = PropertyUtil.convertToBoolean("true", false);
    assertTrue(result);
  }

  @Test
  void convertToBoolean_whenInputIsFalse_returnsFalse() {
    boolean result = PropertyUtil.convertToBoolean("false", false);
    assertFalse(result);
  }

  @Test
  void convertToBoolean_whenInputIsNotBoolean_returnsDefaultValue() {
    boolean result = PropertyUtil.convertToBoolean("not a boolean", false);
    assertFalse(result);
  }

  @Test
  void isTestContainersEnabled_whenPropertyNotSet_returnsTrue() {
    boolean result = PropertyUtil.isTestContainersEnabled();
    assertTrue(result);
  }

  @Test
  @SetSystemProperty(key = "testcontainers.enabled", value = "true")
  void isTestContainersEnabled_whenPropertySet_returnsTrue() {
    boolean result = PropertyUtil.isTestContainersEnabled();
    assertTrue(result);
  }


  @Test
  @SetSystemProperty(key = "testcontainers.enabled", value = "false")
  void isTestContainersEnabled_whenPropertyDisabled_returnsFalse() {
    boolean result = PropertyUtil.isTestContainersEnabled();
    assertFalse(result);
  }

  @Test
  @SetEnvironmentVariable(key = "TESTCONTAINERS_ENABLED", value = "true")
  void isTestContainersEnabled_whenEnvEnabled_returnsFalse() {
    assertTrue(PropertyUtil.isTestContainersEnabled());
  }

  @Test
  @SetEnvironmentVariable(key = "TESTCONTAINERS_ENABLED", value = "false")
  void isTestContainersEnabled_whenEnvDisabled_returnsFalse() {
    assertFalse(PropertyUtil.isTestContainersEnabled());
  }
}
