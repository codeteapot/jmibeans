package com.github.codeteapot.jmibeans.examples.webcert;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSHostName;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSZoneName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;

class WebMachineBuilderProperties implements MachineBuilderPropertiesObject {

  private MachineShellBuilderProperties shell;
  private String dnsZoneNameValue;
  private String dnsHostNameValue;
  private String webAvailableConfDir;
  private String webSiteName;
  private String webCertExtConfigSection;
  private String webCertificatePath;
  private String webPrivateKeyAlgorithm;
  private Integer webPrivateKeySize;
  private String webPrivateKeyPath;

  WebMachineBuilderProperties() {
    shell = new MachineShellBuilderProperties("shell.");
    dnsZoneNameValue = null;
    dnsHostNameValue = null;
    webAvailableConfDir = null;
    webSiteName = null;
    webCertExtConfigSection = null;
    webCertificatePath = null;
    webPrivateKeyAlgorithm = null;
    webPrivateKeySize = null;
    webCertificatePath = null;
  }

  @Override
  public void setProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException {
    switch (name) {
      case "dnsZone":
        dnsZoneNameValue = value.getString();
        break;
      case "dnsHost":
        dnsHostNameValue = value.getString();
        break;
      case "webAvailableConfDir":
        webAvailableConfDir = value.getString();
        break;
      case "webSiteName":
        webSiteName = value.getString();
        break;
      case "webCertExtConfigSection":
        webCertExtConfigSection = value.getString();
        break;
      case "webCertificatePath":
        webCertificatePath = value.getString();
        break;
      case "webPrivateKeyAlgorithm":
        webPrivateKeyAlgorithm = value.getString();
        break;
      case "webPrivateKeySize":
        webPrivateKeySize = value.getInt();
        break;
      case "webPrivateKeyPath":
        webPrivateKeyPath = value.getString();
        break;
      default:
        shell.setProperty(name, value);
    }
  }

  MachineShellBuilderProperties getShell() {
    return shell;
  }

  DNSZoneName getDNSZoneName() throws MachineBuildingException {
    return ofNullable(dnsZoneNameValue)
        .map(DNSZoneName::new)
        .orElseThrow(() -> newUndefinedPropertyException("dnsZone"));
  }

  DNSHostName getDNSHostName() throws MachineBuildingException {
    return new DNSHostName(getDNSZoneName(), ofNullable(dnsHostNameValue)
        .orElseThrow(() -> newUndefinedPropertyException("dnsHost")));
  }

  String getServerName() throws MachineBuildingException {
    return format(
        "%s.%s",
        ofNullable(dnsHostNameValue)
            .orElseThrow(() -> newUndefinedPropertyException("dnsHost")),
        ofNullable(dnsZoneNameValue)
            .orElseThrow(() -> newUndefinedPropertyException("dnsZone")));
  }

  String getWebAvailableConfDir() throws MachineBuildingException {
    return ofNullable(webAvailableConfDir)
        .orElseThrow(() -> newUndefinedPropertyException("webAvailableConfDir"));
  }

  String getWebSiteName() throws MachineBuildingException {
    return ofNullable(webSiteName)
        .orElseThrow(() -> newUndefinedPropertyException("webSiteName"));
  }

  String getWebCertExtConfigSection() throws MachineBuildingException {
    return ofNullable(webCertExtConfigSection)
        .orElseThrow(() -> newUndefinedPropertyException("webCertExtConfigSection"));
  }

  String getWebCertificatePath() throws MachineBuildingException {
    return ofNullable(webCertificatePath)
        .orElseThrow(() -> newUndefinedPropertyException("webCertificatePath"));
  }

  String getWebPrivateKeyAlgorithm() throws MachineBuildingException {
    return ofNullable(webPrivateKeyAlgorithm)
        .orElseThrow(() -> newUndefinedPropertyException("webPrivateKeyAlgorithm"));
  }

  int getWebPrivateKeySize() throws MachineBuildingException {
    return ofNullable(webPrivateKeySize)
        .orElseThrow(() -> newUndefinedPropertyException("webPrivateKeySize"));
  }

  String getWebPrivateKeyPath() throws MachineBuildingException {
    return ofNullable(webPrivateKeyPath)
        .orElseThrow(() -> newUndefinedPropertyException("webPrivateKeyPath"));
  }

  private static MachineBuildingException newUndefinedPropertyException(String propertyName) {
    return new MachineBuildingException(format("Undefined property %s", propertyName));
  }
}
