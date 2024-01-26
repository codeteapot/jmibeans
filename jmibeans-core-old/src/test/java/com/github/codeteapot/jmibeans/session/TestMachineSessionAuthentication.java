package com.github.codeteapot.jmibeans.session;

import static java.util.Collections.unmodifiableSet;

import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthenticationContext;
import com.github.codeteapot.jmibeans.session.MachineSessionPasswordName;
import java.util.Set;

class TestMachineSessionAuthentication implements MachineSessionAuthentication {

  private final Set<MachineSessionPasswordName> passwordNames;

  TestMachineSessionAuthentication(Set<MachineSessionPasswordName> passwordNames) {
    this.passwordNames = unmodifiableSet(passwordNames);
  }

  @Override
  public void authenticate(MachineSessionAuthenticationContext context) {
    passwordNames.forEach(context::addPassword);
  }
}
