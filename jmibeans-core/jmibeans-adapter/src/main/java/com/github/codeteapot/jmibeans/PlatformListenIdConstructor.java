package com.github.codeteapot.jmibeans;

import java.util.function.Function;

@FunctionalInterface
interface PlatformListenIdConstructor {

  PlatformListenId construct(Function<Integer, Integer> randomInt);
}
