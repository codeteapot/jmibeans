package com.github.codeteapot.jmibeans.examples.webcert;

import static java.lang.String.format;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSZoneName;
import com.github.codeteapot.jmibeans.port.light.PlatformPortsLoaderProperties;
import com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType;

class ApplicationSettings {

  private static final String SYSTEM_PROPERTY_PREFIX = "com.github.codeteapot.example.";
  private static final String PORTS_PROPERTY_PREFIX = SYSTEM_PROPERTY_PREFIX.concat("ports.");

  private static final String SHELL_PUBLIC_KEY_URL_FORMAT = "ftp://%s:%s@%s%s";

  private final Properties properties;

  ApplicationSettings() throws IOException {
    properties = new Properties();
    properties.load(getClass().getResourceAsStream("application-settings.properties"));
  }

  DNSZoneName getDNSZoneName() {
    return getProperty("dnsZoneName")
        .map(DNSZoneName::new)
        .orElseThrow(() -> undefinedProperty("dnsZoneName"));
  }

  Path getShellPublicKeyPath() throws URISyntaxException, IOException {
    return get(new URI(format(
        SHELL_PUBLIC_KEY_URL_FORMAT,
        getProperty("shellPublicKeyRepositoryUser")
            .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryUser")),
        lines(getProperty("shellPublicKeyRepositoryPasswordFile")
            .map(Paths::get)
            .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryPasswordFile")))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Password is not available")),
        getProperty("shellPublicKeyRepositoryHost")
            .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryHost")),
        getProperty("shellPublicKeyRepositoryPath")
            .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryPath")))));
  }

  MachineShellPublicKeyType getShellPublicKeyType() {
    return getProperty("shellPublicKeyType")
        .map(MachineShellPublicKeyType::valueOf)
        .orElseThrow(() -> undefinedProperty("shellPublicKeyType"));
  }

  int getShellPublicKeySize() {
    return getProperty("shellPublicKeySize")
        .map(Integer::parseInt)
        .orElseThrow(() -> undefinedProperty("shellPublicKeySize"));
  }

  PlatformPortsLoaderProperties getPortsProperties() {
    return concat(properties.entrySet().stream(), System.getProperties().entrySet().stream())
        .map(entry -> new SimpleEntry<>(entry.getKey().toString(), entry.getValue()))
        .filter(entry -> entry.getKey().startsWith(PORTS_PROPERTY_PREFIX))
        .map(entry -> new SimpleEntry<>(
            entry.getKey().substring(PORTS_PROPERTY_PREFIX.length()),
            entry.getValue()))
        .collect(collectingAndThen(toMap(
            Entry::getKey,
            Entry::getValue), PlatformPortsLoaderProperties::fromMap));
  }

  private Optional<String> getProperty(String key) {
    String value = System.getProperty(SYSTEM_PROPERTY_PREFIX.concat(key));
    if (value != null) {
      return Optional.of(value);
    }
    value = properties.getProperty(key);
    if (value != null) {
      return Optional.of(value);
    }
    return Optional.empty();
  }

  private static IllegalArgumentException undefinedProperty(String propertyName) {
    return new IllegalArgumentException(format("Undefined property %s", propertyName));
  }
}
