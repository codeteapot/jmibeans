package com.github.codeteapot.jmibeans;

/**
 * Maintains the registry of machine builder property converters for a catalog.
 * 
 * @see MachineCatalog#registerBuilderPropertyConverters(MachineBuilderPropertyConverters)
 */
public interface MachineBuilderPropertyConverters {

  /**
   * Add a converter for the specified type.
   *
   * @param <T> Type implemented by the converter.
   *
   * @param type Type for which the converter will be used.
   * @param converter Converter to add.
   *
   * @return {@code true} if it has indeed been added because a converter for the specified type has
   *         not been added previously; {@code false} otherwise.
   */
  <T> boolean register(Class<T> type, MachineBuilderPropertyConverter<T> converter);
}
