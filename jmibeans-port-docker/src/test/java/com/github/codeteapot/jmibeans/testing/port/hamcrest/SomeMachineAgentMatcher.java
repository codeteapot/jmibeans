package com.github.codeteapot.jmibeans.testing.port.hamcrest;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SomeMachineAgentMatcher extends TypeSafeMatcher<MachineAgent> {

  private Matcher<Iterable<? extends MachineNetwork>> networks;

  private SomeMachineAgentMatcher() {
    networks = null;
  }

  public SomeMachineAgentMatcher withNetworks(
      Matcher<Iterable<? extends MachineNetwork>> networks) {
    this.networks = requireNonNull(networks);
    return this;
  }

  @Override
  public void describeTo(Description description) {
    // TODO Matcher description
  }

  public static SomeMachineAgentMatcher someMachineAgent() {
    return new SomeMachineAgentMatcher();
  }

  @Override
  protected boolean matchesSafely(MachineAgent item) {
    if (networks != null && !networks.matches(item.getNetworks())) {
      return false;
    }
    return true;
  }
}
