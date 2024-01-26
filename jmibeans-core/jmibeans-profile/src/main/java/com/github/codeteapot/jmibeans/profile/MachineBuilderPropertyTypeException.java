package com.github.codeteapot.jmibeans.profile;

import static java.util.Objects.requireNonNull;

/**
 * Exception when getting the underlying value of a machine builder property.
 * 
 * <p>
 * This occurs when you try to obtain the underlying value from the raw value using an unsupported
 * type.
 * 
 * @see MachineBuilderPropertyValue
 */
public class MachineBuilderPropertyTypeException extends Exception {

  private static final long serialVersionUID = 1L;

  private final String rawValue;
  private final Class<?> type;

  /**
   * Exception with the specified raw value and unsupported type.
   *
   * @param rawValue Raw value used.
   * @param type Unsupported type.
   */
  public MachineBuilderPropertyTypeException(String rawValue, Class<?> type) {
    super(createMessage(rawValue, type));
    this.rawValue = requireNonNull(rawValue);
    this.type = requireNonNull(type);
  }

  /**
   * Exception with the specified raw value and unsupported type, and incorporating the
   * corresponding cause.
   *
   * @param rawValue Raw value used.
   * @param type Unsupported type.
   * @param cause Cause of the exception.
   */
  public MachineBuilderPropertyTypeException(String rawValue, Class<?> type, Throwable cause) {
    super(createMessage(rawValue, type), cause);
    this.rawValue = requireNonNull(rawValue);
    this.type = requireNonNull(type);
  }

  /**
   * Raw value that has been used.
   *
   * @return The raw value.
   */
  public String getRawValue() {
    return rawValue;
  }

  /**
   * Unsupported type for obtaining underlying value.
   *
   * @return The unsupported type.
   */
  public Class<?> getType() {
    return type;
  }

  private static String createMessage(String rawValue, Class<?> type) {
    return new StringBuilder()
        .append("Property value '").append(rawValue)
        .append("' cannot be parsed as ").append(type)
        .toString();
  }
}
