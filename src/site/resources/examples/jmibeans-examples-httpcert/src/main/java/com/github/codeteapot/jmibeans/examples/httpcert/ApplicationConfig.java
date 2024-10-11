package com.github.codeteapot.jmibeans.examples.httpcert;

import static java.lang.String.format;

import com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

class ApplicationConfig {

  private static final String SYSTEM_PROPERTY_PREFIX = "com.github.codeteapot.example.";

  private final Properties properties;

  ApplicationConfig() throws IOException {
    properties = new Properties();
    properties.load(getClass().getResourceAsStream("application-config.properties"));
  }

  String getDNSZoneName() {
    return getProperty("dnsZoneName")
        .orElseThrow(() -> undefinedProperty("dnsZoneName"));
  }

  String getShellPublicKeyRepositoryHost() {
    return getProperty("shellPublicKeyRepositoryHost")
        .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryHost"));
  }

  String getShellPublicKeyRepositoryUser() {
    return getProperty("shellPublicKeyRepositoryUser")
        .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryUser"));
  }

  File getShellPublicKeyRepositoryPasswordFile() {
    return getProperty("shellPublicKeyRepositoryPasswordFile")
        .map(File::new)
        .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryPasswordFile"));
  }

  String getShellPublicKeyRepositoryPath() {
    return getProperty("shellPublicKeyRepositoryPath")
        .orElseThrow(() -> undefinedProperty("shellPublicKeyRepositoryPath"));
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

  String getDockerPortGroup() {
    return getProperty("dockerPortGroup").orElseThrow(() -> undefinedProperty("dockerPortGroup"));
  }

  String getDockerPortTargetHost() {
    return getProperty("dockerPortTargetHost")
        .orElseThrow(() -> undefinedProperty("dockerPortTargetHost"));
  }

  Optional<Integer> getDockerPortTargetPort() {
    return getProperty("dockerPortTargetPort")
        .map(Integer::parseInt);
  }

  Duration getDockerPortEventsTimeout() {
    return getProperty("dockerPortEventsTimeout")
        .map(Duration::parse)
        .orElseThrow(() -> undefinedProperty("dockerPortEventsTimeout"));
  }

  String getDockerPortNameServerRole() {
    return getProperty("dockerPortNameServerRole")
        .orElseThrow(() -> undefinedProperty("dockerPortNameServerRole"));
  }

  String getDockerPortCertAuthRole() {
    return getProperty("dockerPortCertAuthRole")
        .orElseThrow(() -> undefinedProperty("dockerPortCertAuthRole"));
  }

  String getDockerPortHttpServerRole() {
    return getProperty("dockerPortHttpServerRole")
        .orElseThrow(() -> undefinedProperty("dockerPortHttpServerRole"));
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
