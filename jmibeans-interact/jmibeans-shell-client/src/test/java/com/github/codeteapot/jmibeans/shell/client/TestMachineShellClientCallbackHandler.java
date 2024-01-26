package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellIdentityCallback;

class TestMachineShellClientCallbackHandler implements CallbackHandler {

  private final MachineShellIdentityName identityOnly;
  private final char[] password;
  private boolean supported;

  TestMachineShellClientCallbackHandler(MachineShellIdentityName identityOnly, char[] password) {
    this.identityOnly = requireNonNull(identityOnly);
    this.password = requireNonNull(password);
    supported = true;
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (Callback callback : callbacks) {
      if (supported && callback instanceof NameCallback) {
        handle((NameCallback) callback);
      } else if (callback instanceof PasswordCallback) {
        handle((PasswordCallback) callback);
      } else if (callback instanceof MachineShellIdentityCallback) {
        handle((MachineShellIdentityCallback) callback);
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }

  void setSupported(boolean supported) {
    this.supported = supported;
  }

  private void handle(NameCallback callback) {
    callback.setName(callback.getName().toUpperCase());
  }

  private void handle(PasswordCallback callback) {
    callback.setPassword(password);
  }

  private void handle(MachineShellIdentityCallback callback) {
    callback.setIdentityOnly(identityOnly);
  }
}
