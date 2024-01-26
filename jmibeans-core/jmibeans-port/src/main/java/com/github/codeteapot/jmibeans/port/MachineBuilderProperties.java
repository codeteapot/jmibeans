package com.github.codeteapot.jmibeans.port;

/**
 * Properties in infrastructure that will be used during the build of a machine.
 *
 * <p>
 * It must be implemented by the infrastructure provider.
 *
 * @see MachineLink#getBuilderProperties()
 */
public interface MachineBuilderProperties extends Iterable<MachineBuilderProperty> {
}
