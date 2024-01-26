package com.github.codeteapot.jmibeans.profile;

/**
 * Collaborator in the construction of a machine.
 *
 * <p>
 * Register the facets of a machine when the adapter builds it upon becoming available on the
 * platform.
 *
 * @see MachineProfile#getBuilder()
 */
public interface MachineBuilder extends MachineBuilderPropertiesObject {

  // TODO DOC Return value must be not null
  MachineBuildingResult build(MachineBuilderContext context)
      throws MachineBuildingException, InterruptedException;
}
