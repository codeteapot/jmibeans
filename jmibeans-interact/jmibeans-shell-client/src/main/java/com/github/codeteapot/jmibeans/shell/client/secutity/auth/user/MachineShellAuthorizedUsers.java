package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellIdentityCallback;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class MachineShellAuthorizedUsers implements CallbackHandler {

  private final Supplier<MachineShellAuthentication> authenticationSupplier;

  public MachineShellAuthorizedUsers(
      Function<String, MachineShellUser> userGetter,
      Function<MachineShellPasswordName, char[]> passwordGetter) {
    this(userGetter, passwordGetter, MachineShellAuthentication::new);
  }

  MachineShellAuthorizedUsers(
      Function<String, MachineShellUser> userGetter,
      Function<MachineShellPasswordName, char[]> passwordGetter,
      MachineShellAuthenticationConstructor authenticationConstructor) {
    authenticationSupplier = () -> authenticationConstructor.construct(userGetter, passwordGetter);
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
}
