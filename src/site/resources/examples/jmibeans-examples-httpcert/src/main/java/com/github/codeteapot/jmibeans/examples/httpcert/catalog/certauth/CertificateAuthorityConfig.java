package com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import java.util.Properties;

class CertificateAuthorityConfig {

  private final Properties properties;

  CertificateAuthorityConfig(Properties properties) {
    this.properties = requireNonNull(properties);
  }

  String getBaseDir() throws MachineBuildingException {
    return ofNullable(properties.getProperty("baseDir"))
        .orElseThrow(() -> new MachineBuildingException("Undefined base directory"));
  }
}
