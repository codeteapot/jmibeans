package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.profile.MachineProfile;
import java.util.Optional;

public interface MachineCatalog {

  Optional<MachineProfile> getProfile(MachineProfileName profileName);
}
