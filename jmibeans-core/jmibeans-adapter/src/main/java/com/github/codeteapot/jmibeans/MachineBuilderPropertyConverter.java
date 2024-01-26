package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.port.MachineBuilderProperty;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;

/**
 * Converts the raw value of a machine builder property to a specific type.
 *
 * <p>
 * It is used in the call to {@link MachineBuilderPropertyValue#getObject(Class)} if it is not a
 * built-in type to convert the value obtained using {@link MachineBuilderProperty#getRawValue()}.
 * 
 * @param <T> Type to which the raw value of the property is converted.
 *
 * @see MachineBuilderPropertyConverters
 */
@FunctionalInterface
public interface MachineBuilderPropertyConverter<T> {

  /**
   * Converts the specified raw value to the type implemented by the converter.
   *
   * @param rawValue The raw value to be converted.
   *
   * @return The result of the conversion.
   *
   * @throws Exception In case of an error during the conversion.
   */
  T convert(String rawValue) throws Exception;
}
