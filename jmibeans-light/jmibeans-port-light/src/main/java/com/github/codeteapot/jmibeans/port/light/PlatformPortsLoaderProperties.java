package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

public class PlatformPortsLoaderProperties {

  private final Set<PlatformPortLoaderProperties> entries;

  public PlatformPortsLoaderProperties() {
    this(new HashSet<>());
  }

  PlatformPortsLoaderProperties(Set<PlatformPortLoaderProperties> entries) {
    this.entries = requireNonNull(entries);
  }

  public void addLoader(String type, Map<String, Object> properties) {
    entries.add(new PlatformPortLoaderProperties(type, properties));
  }

  public static PlatformPortsLoaderProperties fromMap(Map<String, Object> map) {
    return fromEntries(map.entrySet().stream());
  }

  public static PlatformPortsLoaderProperties fromProperties(Properties properties) {
    return fromEntries(properties.entrySet().stream()
        .map(entry -> new SimpleEntry<>(entry.getKey().toString(), entry.getValue())));
  }

  Set<PlatformPortLoader> loaders() {
    return entries.stream()
        .map(PlatformPortLoaderProperties::loader)
        .collect(toSet());
  }

  private static PlatformPortsLoaderProperties fromEntries(Stream<Entry<String, Object>> entries) {
    return entries.map(PlatformPortsLoaderProperties::loaderEntry)
        .filter(Objects::nonNull)
        .collect(groupingBy(LoaderEntry::getName))
        .entrySet()
        .stream()
        .map(Entry::getValue)
        .map(List::stream)
        .map(list -> list.reduce(
            new LoaderEntries(),
            LoaderEntries::accumulate,
            LoaderEntries::combine))
        .map(LoaderEntries::finish)
        .filter(Objects::nonNull)
        .collect(collectingAndThen(toSet(), PlatformPortsLoaderProperties::new));
  }

  private static LoaderEntry loaderEntry(Entry<String, Object> entry) {
    try {
      String key = entry.getKey();
      // TODO Check valid property name
      Object value = entry.getValue();
      int index = key.indexOf('.');
      return index == -1
          ? new TypeLoaderEntry(key, value.toString())
          : new PropertyLoaderEntry(key.substring(0, index), key.substring(index + 1), value);
    } catch (ArrayIndexOutOfBoundsException e) {
      // TODO Log invalid property name
      return null;
    }
  }

  private static class LoaderEntries {

    private String type;
    private final Map<String, Object> properties;

    private LoaderEntries() {
      this(null, new HashMap<>());
    }

    private LoaderEntries(String type, Map<String, Object> properties) {
      this.type = type;
      this.properties = requireNonNull(properties);
    }

    private LoaderEntries accumulate(LoaderEntry entry) {
      entry.accumulateTo(this);
      return this;
    }

    private LoaderEntries combine(LoaderEntries other) {
      return new LoaderEntries(ofNullable(other.type).orElse(type), concat(
          properties.entrySet().stream(),
          other.properties.entrySet().stream()).collect(toMap(Entry::getKey, Entry::getValue)));
    }

    private PlatformPortLoaderProperties finish() {
      if (type != null) {
        return new PlatformPortLoaderProperties(type, properties);
      }
      // TODO Log undefined type
      return null;
    }

    private void setType(String type) {
      this.type = type;
    }

    private void putProperty(String name, Object value) {
      properties.put(name, value);
    }
  }

  private static abstract class LoaderEntry {

    private final String name;

    protected LoaderEntry(String name) {
      this.name = requireNonNull(name);
    }

    private String getName() {
      return name;
    }

    protected abstract LoaderEntries accumulateTo(LoaderEntries entries);
  }

  private static class TypeLoaderEntry extends LoaderEntry {

    private final String type;

    private TypeLoaderEntry(String name, String type) {
      super(name);
      this.type = requireNonNull(type);
    }

    @Override
    protected LoaderEntries accumulateTo(LoaderEntries entries) {
      entries.setType(type);
      return entries;
    }
  }

  private static class PropertyLoaderEntry extends LoaderEntry {

    private final String propName;
    private final Object propValue;

    private PropertyLoaderEntry(String name, String propName, Object propValue) {
      super(name);
      this.propName = requireNonNull(propName);
      this.propValue = requireNonNull(propValue);
    }

    @Override
    protected LoaderEntries accumulateTo(LoaderEntries entries) {
      entries.putProperty(propName, propValue);
      return entries;
    }
  }
}
