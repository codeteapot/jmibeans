package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;

/**
 * Main element of the infrastructure provider.
 *
 * <p>
 * It must be implemented by the infrastructure provider along with
 * <ul>
 * <li>{@link MachineLink},</li>
 * <li>{@link MachineBuilderProperties},</li>
 * <li>{@link MachineAgent} and</li>
 * <li>{@link MachineNetwork}.</li>
 * </ul>
 * 
 * <p>
 * Its objective is to stay abreast of infrastructure changes in order to manage machine
 * availability on the platform using the {@link MachineManager} provided by it.
 */
public interface PlatformPort {

  /**
   * It keeps the platform up to date on the availability of machines in the infrastructure.
   *
   * <p>
   * When a machine becomes available, it calls {@link MachineManager#accept(byte[], MachineLink)}
   * with the machine identifier in the infrastructure scope and the elements necessary to build the
   * machine in terms of platform.
   *
   * <p>
   * When a machine becomes unavailable, it calls {@link MachineManager#forget(byte[])} with the
   * machine identifier in the infrastructure scope.
   *
   * <p>
   * It also calls {@link MachineManager#accept(byte[], MachineLink)} for all machines that existed
   * before this method call, as if they had become available at that moment.
   *
   * <p>
   * This is a blocking operation. It can continue running until the thread in which it is executing
   * is interrupted.
   *
   * @param manager Platform machine manager.
   *
   * @throws InterruptedException When the execution of the operation is interrupted.
   */
  void listen(MachineManager manager) throws InterruptedException;
}
