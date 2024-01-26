package com.github.codeteapot.jmibeans.port.docker;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.logging.Logger.getLogger;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

class AwaitCompletionFix {

  private static final Logger logger = getLogger(DockerPlatformPort.class.getName());

  private final Instant initial;

  AwaitCompletionFix() {
    initial = now();
  }

  void apply(Duration timeout) throws InterruptedException {
    Duration remaining = timeout.minus(between(initial, now()));
    if (remaining.compareTo(ZERO) > 0) {
      logger.fine(format("%s remaining await time is greater than zero", remaining));
      sleep(remaining.toMillis());
    }
  }
}
