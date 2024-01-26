package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import java.util.Properties;

class WebServerConfig {

  private final Properties properties;

  WebServerConfig(Properties properties) {
    this.properties = requireNonNull(properties);
  }

  String getServerName() throws MachineBuildingException {
    return ofNullable(properties.getProperty("serverName"))
        .orElseThrow(() -> new MachineBuildingException("Undefined server name"));
  }

  String getSiteName() throws MachineBuildingException {
    return ofNullable(properties.getProperty("siteName"))
        .orElseThrow(() -> new MachineBuildingException("Undefined site name"));
  }

  String getCertificatePath() throws MachineBuildingException {
    return ofNullable(properties.getProperty("certificatePath"))
        .orElseThrow(() -> new MachineBuildingException("Undefined certificate path"));
  }

  String getPrivateKeyAlgorithm() throws MachineBuildingException {
    return ofNullable(properties.getProperty("privateKeyAlgorithm"))
        .orElseThrow(() -> new MachineBuildingException("Undefined private key algorithm"));
  }

  int getPrivateKeySize() throws MachineBuildingException {
    try {
      return ofNullable(properties.getProperty("privateKeySize"))
          .map(Integer::parseInt)
          .orElseThrow(() -> new MachineBuildingException("Undefined private key size"));
    } catch (NumberFormatException e) {
      throw new MachineBuildingException("Bad private key size", e);
    }
  }

  String getPrivateKeyPath() throws MachineBuildingException {
    return ofNullable(properties.getProperty("privateKeyPath"))
        .orElseThrow(() -> new MachineBuildingException("Undefined private key path"));
  }
}
