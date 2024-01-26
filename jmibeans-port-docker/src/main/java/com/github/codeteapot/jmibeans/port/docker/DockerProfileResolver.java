package com.github.codeteapot.jmibeans.port.docker;

import java.util.Optional;

import com.github.codeteapot.jmibeans.port.MachineProfileName;

public interface DockerProfileResolver {

  MachineProfileName getDefault();

  // TODO DESIGN Many roles allowed (Set<String> roleNames)
  Optional<MachineProfileName> fromRole(String roleName);
}
