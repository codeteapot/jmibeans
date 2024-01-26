package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class PlatformPortIdGenerator {

  private final Random random;
  private final Set<PlatformPortId> alreadyUsed;
  private final PlatformPortIdConstructor portIdConstructor;

  public PlatformPortIdGenerator() {
    this(new Random(), new HashSet<>(), PlatformPortId::new);
  }

  PlatformPortIdGenerator(
      Random random,
      Set<PlatformPortId> alreadyUsed,
      PlatformPortIdConstructor portIdConstructor) {
    this.random = requireNonNull(random);
    this.alreadyUsed = requireNonNull(alreadyUsed);
    this.portIdConstructor = requireNonNull(portIdConstructor);
  }

  PlatformPortId generate() {
    PlatformPortId portId = portIdConstructor.construct(random);
    while (alreadyUsed.contains(portId)) {
      portId = portIdConstructor.construct(random);
    }
    alreadyUsed.add(portId);
    return portId;
  }
}
