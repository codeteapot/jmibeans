package com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback;

import static java.util.Optional.ofNullable;
import java.util.Optional;
import javax.security.auth.callback.Callback;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.MachineShellIdentityName;

public class MachineShellIdentityCallback implements Callback {

  private MachineShellIdentityName identityOnly;

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
