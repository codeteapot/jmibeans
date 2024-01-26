package com.github.codeteapot.jmibeans.examples.webcert;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;

class CertMachineBuilderProperties implements MachineBuilderPropertiesObject {

  private MachineShellBuilderProperties shell;
  private String caBaseDir;

  CertMachineBuilderProperties() {
    shell = new MachineShellBuilderProperties("shell.");
    caBaseDir = null;
  }

  @Override
  public void setProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException {
    switch (name) {
      case "caBaseDir":
        caBaseDir = value.getString();
        break;
      default:
        shell.setProperty(name, value);
    }
  }

  MachineShellBuilderProperties getShell() {
    return shell;
  }

  String getCABaseDir() throws MachineBuildingException {
    return ofNullable(caBaseDir)
        .orElseThrow(() -> newUndefinedPropertyException("caBaseDir"));
  }

  private static MachineBuildingException newUndefinedPropertyException(String propertyName) {
    return new MachineBuildingException(format("Undefined property %s", propertyName));
  }
}
