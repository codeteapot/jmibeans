package com.github.codeteapot.jmibeans.platform;

import java.util.Optional;
import java.util.stream.Stream;

public interface PlatformContext {

  Stream<Machine> available();

  Optional<Machine> lookup(MachineRef ref);
}
