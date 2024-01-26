package com.github.codeteapot.jmibeans.port.docker.role;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.docker.DockerProfileResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DirectMappingDockerProfileResolver implements DockerProfileResolver {

  private final MachineProfileName defaultProfile;
  final Map<String, MachineProfileName> profileMap;

  public DirectMappingDockerProfileResolver(MachineProfileName defaultProfile) {
    this.defaultProfile = requireNonNull(defaultProfile);
    this.profileMap = new HashMap<>();
  }

  @Override
  public MachineProfileName getDefault() {
    return defaultProfile;
  }

  @Override
  public Optional<MachineProfileName> fromRole(String roleName) {
    return ofNullable(profileMap.get(roleName));
  }

  public DirectMappingDockerProfileResolver withMapping(
      String roleName,
      MachineProfileName profileName) {
    profileMap.put(roleName, profileName);
    return this;
  }
}
