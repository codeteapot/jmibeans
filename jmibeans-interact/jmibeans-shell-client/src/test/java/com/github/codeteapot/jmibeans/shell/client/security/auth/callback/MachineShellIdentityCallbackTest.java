package com.github.codeteapot.jmibeans.shell.client.security.auth.callback;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;

class MachineShellIdentityCallbackTest {

  private static final MachineShellIdentityName SOME_IDENTITY_ONLY = new MachineShellIdentityName(
      "some-identity");

  private MachineShellIdentityCallback callback;

  @BeforeEach
  void setUp() {
    callback = new MachineShellIdentityCallback();
  }

  void getIdentityOnly() {
    callback.identityOnly = SOME_IDENTITY_ONLY;

    Optional<MachineShellIdentityName> identityOnly = callback.getIdentityOnly();

    assertThat(identityOnly).hasValue(SOME_IDENTITY_ONLY);
  }

  void setIdentityOnly() {
    callback.setIdentityOnly(SOME_IDENTITY_ONLY);

    assertThat(callback.identityOnly).isEqualTo(SOME_IDENTITY_ONLY);
  }
}
