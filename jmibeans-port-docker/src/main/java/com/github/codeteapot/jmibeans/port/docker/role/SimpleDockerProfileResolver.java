package com.github.codeteapot.jmibeans.port.docker.role;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.docker.DockerProfileResolver;
import java.util.Optional;
import java.util.function.Function;

public class SimpleDockerProfileResolver implements DockerProfileResolver {

  private final Function<String, MachineProfileName> mapper;
  private final MachineProfileName defaultProfile;

  public SimpleDockerProfileResolver(
      Function<String, MachineProfileName> mapper,
      MachineProfileName defaultProfile) {
    this.mapper = requireNonNull(mapper);
    this.defaultProfile = requireNonNull(defaultProfile);
  }

  @Override
  public MachineProfileName getDefault() {
    return defaultProfile;
  }

  @Override
  public Optional<MachineProfileName> fromRole(String roleName) {
    return ofNullable(mapper.apply(roleName));
  }
}
