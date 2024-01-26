package com.github.codeteapot.jmibeans;

import java.util.function.Function;

@FunctionalInterface
interface PlatformPortIdConstructor {

  PlatformPortId construct(Function<Integer, Integer> randomInt);
}
