package com.github.codeteapot.jmibeans;

import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO Missing Javadoc
public class MachineCatalogDefinition implements MachineCatalog {

  private final Map<MachineProfileName, MachineProfile> profileMap;

  public MachineCatalogDefinition() {
    profileMap = new HashMap<>();
  }

  @Override
  public Optional<MachineProfile> getProfile(MachineProfileName profileName) {
    return ofNullable(profileMap.get(profileName));
  }

  public MachineCatalogDefinition withProfile(
      MachineProfileName profileName,
      MachineBuilder builder) {
    profileMap.put(profileName, new MachineProfileDefinition(builder));
    return this;
  }
}
