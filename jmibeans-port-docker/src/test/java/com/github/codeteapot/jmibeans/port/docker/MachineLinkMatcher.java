package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;

import org.mockito.ArgumentMatcher;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;

class MachineLinkMatcher implements ArgumentMatcher<MachineLink> {

  private MachineProfileName profileName;
  private ArgumentMatcher<MachineAgent> agentMatcher;

  MachineLinkMatcher() {
    profileName = null;
    agentMatcher = null;
  }

  MachineLinkMatcher withProfileName(MachineProfileName profileName) {
    this.profileName = requireNonNull(profileName);
    return this;
  }

  MachineLinkMatcher withAgent(ArgumentMatcher<MachineAgent> agentMatcher) {
    this.agentMatcher = requireNonNull(agentMatcher);
    return this;
  }

  @Override
  public boolean matches(MachineLink argument) {
    if (profileName != null && !profileName.equals(argument.getProfileName())) {
      return false;
    }
    if (agentMatcher != null && !agentMatcher.matches(argument.getAgent())) {
      return false;
    }
    return true;
  }
}
