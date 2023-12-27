package com.github.codeteapot.jmibeans;

import java.time.Duration;
import java.util.Optional;

/**
 * Properties of the session pool created to establish sessions on a machine.
 *
 * @see MachineProfile#getSessionPool()
 */
public interface MachineSessionPool {

  /**
   * The time that an idle session can be kept alive until it is used again.
   *
   * @return The time that a session is held can be kept idle. Empty in case of using the default
   *         time.
   */
  Optional<Duration> getIdleTimeout();
}
