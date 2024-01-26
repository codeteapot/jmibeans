package com.github.codeteapot.jmibeans.profile;

/**
 * Object that receives machine builder properties.
 */
public interface MachineBuilderPropertiesObject {

  /**
   * Sets the value of a property with the specified name.
   *
   * @param name Name of the property to be modified.
   * @param value New property value.
   *
   * @throws MachineBuilderPropertyTypeException If an error occurs when obtaining the underlying
   *         property value.
   */
  void setProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException;
}
