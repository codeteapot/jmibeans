package com.github.codeteapot.jmibeans.port.docker;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import java.util.Optional;

public interface DockerProfileResolver {

  MachineProfileName getDefault();

  Optional<MachineProfileName> fromRole(String roleName);
}
