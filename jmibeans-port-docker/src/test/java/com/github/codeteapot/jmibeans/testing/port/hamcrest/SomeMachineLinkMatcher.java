package com.github.codeteapot.jmibeans.testing.port.hamcrest;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SomeMachineLinkMatcher extends TypeSafeMatcher<MachineLink> {

  private Matcher<MachineProfileName> profileName;
  private Matcher<MachineAgent> agentMatcher;

  private SomeMachineLinkMatcher() {
    profileName = null;
    agentMatcher = null;
  }

  public SomeMachineLinkMatcher withProfileName(Matcher<MachineProfileName> profileName) {
    this.profileName = requireNonNull(profileName);
    return this;
  }

  public SomeMachineLinkMatcher withAgent(Matcher<MachineAgent> agentMatcher) {
    this.agentMatcher = requireNonNull(agentMatcher);
    return this;
  }

  @Override
  public void describeTo(Description description) {
    // TODO Matcher description
  }

  public static SomeMachineLinkMatcher someMachineLink() {
    return new SomeMachineLinkMatcher();
  }

  @Override
  protected boolean matchesSafely(MachineLink item) {
    if (profileName != null && !profileName.matches(item.getProfileName())) {
      return false;
    }
    if (agentMatcher != null && !agentMatcher.matches(item.getAgent())) {
      return false;
    }
    return true;
  }
}
