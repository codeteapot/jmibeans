package com.github.codeteapot.jmibeans.testing.port.hamcrest;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SomeMachineNetworkMatcher extends TypeSafeMatcher<MachineNetwork> {

  private Matcher<MachineNetworkName> name;
  private Matcher<InetAddress> address;

  private SomeMachineNetworkMatcher() {
    name = null;
    address = null;
  }

  public SomeMachineNetworkMatcher withName(Matcher<MachineNetworkName> name) {
    this.name = requireNonNull(name);
    return this;
  }

  public SomeMachineNetworkMatcher withAddress(Matcher<InetAddress> address) {
    this.address = requireNonNull(address);
    return this;
  }

  @Override
  public void describeTo(Description description) {
    // TODO Matcher description
  }

  public static SomeMachineNetworkMatcher someMachineNetwork() {
    return new SomeMachineNetworkMatcher();
  }

  @Override
  protected boolean matchesSafely(MachineNetwork item) {
    try {
      if (name != null && !name.matches(item.getName())) {
        return false;
      }
      if (address != null && !address.matches(item.getAddress())) {
        return false;
      }
      return true;
    } catch (UnknownHostException e) {
      return false;
    }
  }
}
