package com.github.codeteapot.jmibeans.profile.light;

import static java.util.Objects.requireNonNull;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;

public abstract class PrefixedMachineBuilderProperties implements MachineBuilderPropertiesObject {

  protected final String prefix;

  protected PrefixedMachineBuilderProperties(String prefix) {
    this.prefix = requireNonNull(prefix);
  }

  @Override
  public final void setProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException {
    if (name.startsWith(prefix)) {
      setPrefixedProperty(name.substring(prefix.length()), value);
    }
  }

  protected abstract void setPrefixedProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException;
}
