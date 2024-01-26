package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellIdentityCallback;
import java.io.IOException;
import java.util.function.Supplier;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class MachineShellAuthorizedUsers implements CallbackHandler {

  private final Supplier<MachineShellAuthentication> authenticationSupplier;

  public MachineShellAuthorizedUsers(MachineShellUserRepository repository) {
    this(repository, MachineShellAuthentication::new);
  }

  MachineShellAuthorizedUsers(
      MachineShellUserRepository repository,
      MachineShellAuthenticationConstructor authenticationConstructor) {
    authenticationSupplier = () -> authenticationConstructor.construct(repository);
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    MachineShellAuthentication authentication = authenticationSupplier.get();
    for (Callback callback : callbacks) {
      if (callback instanceof NameCallback) {
        authentication.handle((NameCallback) callback);
      } else if (callback instanceof MachineShellIdentityCallback) {
        authentication.handle((MachineShellIdentityCallback) callback);
      } else if (callback instanceof PasswordCallback) {
        authentication.handle((PasswordCallback) callback);
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }

  // private void handle(MachineShellAuthentication authentication, NameCallback callback) {
  // callback.setName(authentication.setName(callback.getName()));
  // }
  //
  // private void handle(
  // MachineShellAuthentication authentication,
  // MachineShellIdentityCallback callback) {
  // authentication.getIdentityOnly().ifPresent(callback::setIdentityOnly);
  // }
  //
  // private void handle(MachineShellAuthentication authentication, PasswordCallback callback) {
  // authentication.getPassword().ifPresent(callback::setPassword);
  // }
}
