package com.github.codeteapot.jmibeans.examples.webcert;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import java.util.Optional;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.light.PrefixedMachineBuilderProperties;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellUser;

public class MachineShellBuilderProperties extends PrefixedMachineBuilderProperties {

  private MachineNetworkName networkName;
  private Integer port;
  private MachineShellUser user;

  public MachineShellBuilderProperties(String prefix) {
    super(prefix);
  }

  @Override
  protected void setPrefixedProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException {
    switch (name) {
      case "network":
        networkName = new MachineNetworkName(value.getString());
        break;
      case "port":
        port = value.getInt();
        break;
      case "user":
        user = new MachineShellUser(value.getString());
        break;
    }
  }

  MachineNetworkName getNetworkName() throws MachineBuildingException {
    return ofNullable(networkName)
        .orElseThrow(() -> newUndefinedPropertyException(prefix.concat("network")));
  }

  Optional<Integer> getPort() {
    return ofNullable(port);
  }

  MachineShellUser getUser() throws MachineBuildingException {
    return ofNullable(user)
        .orElseThrow(() -> newUndefinedPropertyException(prefix.concat("user")));
  }

  private static MachineBuildingException newUndefinedPropertyException(String propertyName) {
    return new MachineBuildingException(format("Undefined property %s", propertyName));
  }
}
