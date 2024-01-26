package com.github.codeteapot.jmibeans.profile;

import java.io.Serializable;

/**
 * Container for the value of a machine builder property.
 *
 * <p>
 * It facilitates obtaining the underlying value with a specific type from the raw value, which is
 * the {@link String} representation of the value.
 *
 * @see MachineBuilderPropertiesObject#setProperty(String, MachineBuilderPropertyValue)
 */
public interface MachineBuilderPropertyValue extends Serializable {

  /**
   * Obtains the value as a string of characters.
   *
   * @return The value as a string of characters.
   *
   * @throws MachineBuilderPropertyTypeException If there is a problem returning the raw value
   *         ultimately.
   */
  String getString() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value as a boolean.
   *
   * @return The value as a boolean.
   *
   * @throws MachineBuilderPropertyTypeException If the raw value cannot be parsed as a boolean.
   */
  boolean getBoolean() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value as a short integer.
   *
   * @return The value as a short integer.
   *
   * @throws MachineBuilderPropertyTypeException If the raw value cannot be parsed as a short
   *         integer.
   */
  short getShort() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value as an integer.
   *
   * @return The value as an integer.
   *
   * @throws MachineBuilderPropertyTypeException If the raw value cannot be parsed as an integer.
   */
  int getInt() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value as a long integer.
   *
   * @return The value as a long integer.
   *
   * @throws MachineBuilderPropertyTypeException If the raw value cannot be parsed as a long
   *         integer.
   */
  long getLong() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value as a floating-point number.
   *
   * @return The value as a floating-point number.
   *
   * @throws MachineBuilderPropertyTypeException If the raw value cannot be parsed as a
   *         floating-point number.
   */
  float getFloat() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value as a double floating-point number.
   * 
   * @return The value as a double floating-point number.
   *
   * @throws MachineBuilderPropertyTypeException If the raw value cannot be parsed as a double
   *         floating-point number.
   */
  double getDouble() throws MachineBuilderPropertyTypeException;

  /**
   * Obtains the value with the specified type.
   *
   * <p>
   * Try to obtain the most suitable simple type value. If there is no match, try using the
   * converters registered in the catalog.
   *
   * @param <T> Type in which the value has to be obtained.
   *
   * @param type Instance of the type in which the value has to be obtained.
   *
   * @return The value as the specified type.
   *
   * @throws MachineBuilderPropertyTypeException If it is not supported as a simple type or there is
   *         no successful converter.
   */
  <T> T getObject(Class<T> type) throws MachineBuilderPropertyTypeException;
}
