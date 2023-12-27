package com.github.codeteapot.jmibeans.port;

import com.github.codeteapot.jmibeans.PlatformAdapter;

/**
 * Bridge between platform and infrastructure.
 *
 * @see PlatformAdapter#listen(PlatformPort)
 */
public interface PlatformPort {

  /**
   * Keeps the port aware of infrastructure changes.
   *
   * <p>This is a blocking operation. The listening process stays active indefinitely, until the
   * thread in which it is running is interrupted.
   *
   * @param manager Machine manager through which changes are notified to the platform.
   *
   * @throws InterruptedException When the thread in which the listener process is running is
   *         interrupted.
   */
  void listen(MachineManager manager) throws InterruptedException;
}
