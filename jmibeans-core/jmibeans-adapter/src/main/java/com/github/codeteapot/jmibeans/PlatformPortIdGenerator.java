package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

class PlatformPortIdGenerator {

  private final Function<Integer, Integer> randomInt;
  private final Set<PlatformPortId> alreadyUsed;
  private final PlatformPortIdConstructor portIdConstructor;

  public PlatformPortIdGenerator() {
    this(new Random()::nextInt, new HashSet<>(), PlatformPortId::new);
  }

  PlatformPortIdGenerator(
      Function<Integer, Integer> randomInt,
      Set<PlatformPortId> alreadyUsed,
      PlatformPortIdConstructor portIdConstructor) {
    this.randomInt = requireNonNull(randomInt);
    this.alreadyUsed = requireNonNull(alreadyUsed);
    this.portIdConstructor = requireNonNull(portIdConstructor);
  }

  PlatformPortId generate() {
    PlatformPortId portId = portIdConstructor.construct(randomInt);
    while (alreadyUsed.contains(portId)) {
      portId = portIdConstructor.construct(randomInt);
    }
    alreadyUsed.add(portId);
    return portId;
  }
}
