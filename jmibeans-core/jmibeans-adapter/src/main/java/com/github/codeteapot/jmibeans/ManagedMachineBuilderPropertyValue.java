package com.github.codeteapot.jmibeans;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;
import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;

class ManagedMachineBuilderPropertyValue implements MachineBuilderPropertyValue {

  private static final long serialVersionUID = 1L;

  private final MachineContainerBuilderPropertyConverters converters;
  private final String rawValue;

  ManagedMachineBuilderPropertyValue(
      MachineContainerBuilderPropertyConverters converters,
      String rawValue) {
    this.converters = requireNonNull(converters);
    this.rawValue = requireNonNull(rawValue);
  }

  @Override
  public String getString() throws MachineBuilderPropertyTypeException {
    return rawValue;
  }

  @Override
  public boolean getBoolean() throws MachineBuilderPropertyTypeException {
    return parseBoolean(rawValue);
  }

  @Override
  public short getShort() throws MachineBuilderPropertyTypeException {
    try {
      return parseShort(rawValue);
    } catch (NumberFormatException e) {
      throw new MachineBuilderPropertyTypeException(rawValue, Short.class, e);
    }
  }

  @Override
  public int getInt() throws MachineBuilderPropertyTypeException {
    try {
      return parseInt(rawValue);
    } catch (NumberFormatException e) {
      throw new MachineBuilderPropertyTypeException(rawValue, Integer.class, e);
    }
  }

  @Override
  public long getLong() throws MachineBuilderPropertyTypeException {
    try {
      return parseLong(rawValue);
    } catch (NumberFormatException e) {
      throw new MachineBuilderPropertyTypeException(rawValue, Long.class, e);
    }
  }

  @Override
  public float getFloat() throws MachineBuilderPropertyTypeException {
    try {
      return parseFloat(rawValue);
    } catch (NumberFormatException e) {
      throw new MachineBuilderPropertyTypeException(rawValue, Float.class, e);
    }
  }

  @Override
  public double getDouble() throws MachineBuilderPropertyTypeException {
    try {
      return parseDouble(rawValue);
    } catch (NumberFormatException e) {
      throw new MachineBuilderPropertyTypeException(rawValue, Double.class, e);
    }
  }

  @Override
  public <T> T getObject(Class<T> type) throws MachineBuilderPropertyTypeException {
    try {
      if (type.isAssignableFrom(Double.class)) {
        return type.cast(getDouble());
      }
      if (type.isAssignableFrom(Float.class)) {
        return type.cast(getFloat());
      }
      if (type.isAssignableFrom(Long.class)) {
        return type.cast(getLong());
      }
      if (type.isAssignableFrom(Integer.class)) {
        return type.cast(getInt());
      }
      if (type.isAssignableFrom(Short.class)) {
        return type.cast(getShort());
      }
      if (type.isAssignableFrom(Boolean.class)) {
        return type.cast(getBoolean());
      }
      if (type.isAssignableFrom(String.class)) {
        return type.cast(getString());
      }
      return converters.convert(rawValue, type)
          .getValue()
          .orElseThrow(() -> new MachineBuilderPropertyTypeException(rawValue, type));
    } catch (Exception e) {
      throw new MachineBuilderPropertyTypeException(rawValue, type, e);
    }
  }
}
