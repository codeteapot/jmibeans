package com.github.codeteapot.jmibeans.port.docker;

import com.github.codeteapot.jmibeans.port.MachineId;

@FunctionalInterface
interface MachineIdConstructor {

  MachineId construct(byte[] value);
}
