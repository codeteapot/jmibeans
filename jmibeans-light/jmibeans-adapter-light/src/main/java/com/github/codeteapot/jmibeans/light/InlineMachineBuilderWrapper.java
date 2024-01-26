package com.github.codeteapot.jmibeans.light;

import static java.util.Objects.requireNonNull;
import java.util.function.Function;
import com.github.codeteapot.jmibeans.light.MachineCatalogDefinition.InlineMachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertiesObject;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyTypeException;
import com.github.codeteapot.jmibeans.profile.MachineBuilderPropertyValue;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.MachineBuildingResult;

class InlineMachineBuilderWrapper<P extends MachineBuilderPropertiesObject>
    implements MachineBuilder {

  private final P propertiesObj;
  private final Function<P, InlineMachineBuilder> inlineMapper;

  InlineMachineBuilderWrapper(
      P propertiesObj,
      Function<P, InlineMachineBuilder> inlineMapper) {
    this.propertiesObj = requireNonNull(propertiesObj);
    this.inlineMapper = requireNonNull(inlineMapper);
  }

  @Override
  public void setProperty(String name, MachineBuilderPropertyValue value)
      throws MachineBuilderPropertyTypeException {
    propertiesObj.setProperty(name, value);
  }

  @Override
  public MachineBuildingResult build(MachineBuilderContext context)
      throws MachineBuildingException, InterruptedException {
    return inlineMapper.apply(propertiesObj).build(context);
  }
}
