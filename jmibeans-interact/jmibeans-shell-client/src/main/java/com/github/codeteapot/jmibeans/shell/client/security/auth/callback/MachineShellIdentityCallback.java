package com.github.codeteapot.jmibeans.shell.client.security.auth.callback;

import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.util.Optional;
import javax.security.auth.callback.Callback;

public class MachineShellIdentityCallback implements Callback {

  MachineShellIdentityName identityOnly;

  public MachineShellIdentityCallback() {
    identityOnly = null;
  }

  public Optional<MachineShellIdentityName> getIdentityOnly() {
    return ofNullable(identityOnly);
  }

  public void setIdentityOnly(MachineShellIdentityName identityOnly) {
    this.identityOnly = identityOnly;
  }
}
