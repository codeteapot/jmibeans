package com.github.codeteapot.jmibeans.examples.webcert;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSZoneName;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;

class DNSMachineBuilderProperties implements MachineBuilderPropertiesObject {

  private MachineShellBuilderProperties shell;
  private MachineNetworkName dnsNetworkName;
  private String dnsZonesScript;
  private String dnsZoneNameValue;

  DNSMachineBuilderProperties() {
    shell = new MachineShellBuilderProperties("shell.");
    dnsNetworkName = null;
    dnsZonesScript = null;
    dnsZoneNameValue = null;
  }

  @Override
  public void setProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException {
    switch (name) {
      case "dnsNetwork":
        dnsNetworkName = new MachineNetworkName(value.getString());
        break;
      case "dnsZonesScript":
        dnsZonesScript = value.getString();
        break;
      case "dnsZone":
        dnsZoneNameValue = value.getString();
        break;
      default:
        shell.setProperty(name, value);
    }
  }

  MachineShellBuilderProperties getShell() {
    return shell;
  }

  MachineNetworkName getDNSNetworkName() throws MachineBuildingException {
    return ofNullable(dnsNetworkName)
        .orElseThrow(() -> newUndefinedPropertyException("dnsNetwork"));
  }

  DNSZoneName getDNSZoneName() throws MachineBuildingException {
    return ofNullable(dnsZoneNameValue)
        .map(DNSZoneName::new)
        .orElseThrow(() -> newUndefinedPropertyException("dnsZone"));
  }

  String getDNSZonesScript() throws MachineBuildingException {
    return ofNullable(dnsZonesScript)
        .orElseThrow(() -> newUndefinedPropertyException("dnsZonesScript"));
  }

  private static MachineBuildingException newUndefinedPropertyException(String propertyName) {
    return new MachineBuildingException(format("Undefined property %s", propertyName));
  }
}
