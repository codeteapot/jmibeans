package com.github.codeteapot.jmibeans;

import java.util.Random;

@FunctionalInterface
interface PlatformPortIdConstructor {

  PlatformPortId construct(Random random);
}
