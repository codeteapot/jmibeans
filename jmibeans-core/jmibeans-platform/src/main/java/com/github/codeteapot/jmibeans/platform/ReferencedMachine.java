package com.github.codeteapot.jmibeans.platform;

/**
 * Machine available on the platform with its reference.
 *
 * @see PlatformContext#available()
 */
public interface ReferencedMachine extends Machine {

  /**
   * Machine reference.
   *
   * @return The machine reference.
   */
  public MachineRef getRef();
}
