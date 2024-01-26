package com.github.codeteapot.jmibeans;

import java.util.HashSet;
import java.util.Set;

class MachineContainerBuilderPropertyConverters implements MachineBuilderPropertyConverters {

  private final Set<MachineContainerBuilderPropertyConverter> entries;

  MachineContainerBuilderPropertyConverters() {
    entries = new HashSet<>();
  }

  @Override
  public <T> boolean register(Class<T> type, MachineBuilderPropertyConverter<T> converter) {
    return entries.add(new MachineContainerBuilderPropertyConverter(type, converter));
  }

  <T> MachineContainerBuilderPropertyConverterResult<T> convert(String rawValue, Class<T> type) {
    return entries.stream()
        .filter(converter -> converter.match(type))
        .findAny()
        .map(converter -> convert(converter, rawValue, type))
        .orElseGet(MachineContainerBuilderPropertyConverterResult::new);
  }

  private static <T> MachineContainerBuilderPropertyConverterResult<T> convert(
      MachineContainerBuilderPropertyConverter converter,
      String rawValue,
      Class<T> type) {
    try {
      return new MachineContainerBuilderPropertyConverterResult<>(converter.convert(
          rawValue,
          type));
    } catch (Exception e) {
      return new MachineContainerBuilderPropertyConverterResult<>(e);
    }
  }
}
