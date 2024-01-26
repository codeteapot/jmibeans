package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

class PlatformListenIdGenerator {

  private final Function<Integer, Integer> randomInt;
  private final Set<PlatformListenId> alreadyUsed;
  private final PlatformListenIdConstructor listenIdConstructor;

  public PlatformListenIdGenerator() {
    this(new Random()::nextInt, new HashSet<>(), PlatformListenId::new);
  }

  PlatformListenIdGenerator(
      Function<Integer, Integer> randomInt,
      Set<PlatformListenId> alreadyUsed,
      PlatformListenIdConstructor listenIdConstructor) {
    this.randomInt = requireNonNull(randomInt);
    this.alreadyUsed = requireNonNull(alreadyUsed);
    this.listenIdConstructor = requireNonNull(listenIdConstructor);
  }

  PlatformListenId generate() {
    PlatformListenId listenId = listenIdConstructor.construct(randomInt);
    while (alreadyUsed.contains(listenId)) {
      listenId = listenIdConstructor.construct(randomInt);
    }
    alreadyUsed.add(listenId);
    return listenId;
  }
}
