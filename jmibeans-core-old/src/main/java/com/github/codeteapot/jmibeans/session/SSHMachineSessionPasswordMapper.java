package com.github.codeteapot.jmibeans.session;

import java.util.Optional;

@FunctionalInterface
interface SSHMachineSessionPasswordMapper {

  Optional<byte[]> map(MachineSessionPasswordName passwordName);
}
