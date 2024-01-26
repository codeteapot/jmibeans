package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellIdentityCallback;
import java.io.IOException;
import java.net.InetAddress;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class MachineShellClient implements MachineShellClientConnectionFactory {

  private final MachineShellClientContext context;
  private final InetAddress host;
  private final Integer port;
  private final CallbackHandler callbackHandler;

  public MachineShellClient(
      MachineShellClientContext context,
      InetAddress host,
      Integer port,
      CallbackHandler callbackHandler) {
    this.context = requireNonNull(context);
    this.host = requireNonNull(host);
    this.port = port;
    this.callbackHandler = requireNonNull(callbackHandler);
  }

  @Override
  public MachineShellClientConnection getConnection(String username)
      throws MachineShellClientException {
    try {
      NameCallback nameCallback = new NameCallback("User");
      nameCallback.setName(username);
      PasswordCallback passwordCallback = new PasswordCallback("Password", false);
      MachineShellIdentityCallback identityCallback = new MachineShellIdentityCallback();
      callbackHandler.handle(new Callback[] {
          nameCallback,
          passwordCallback,
          identityCallback
      });
      return context.getConnection(new MachineShellClientContextConnectionSpecImpl(
          host,
          port,
          nameCallback,
          identityCallback,
          passwordCallback));
    } catch (UnsupportedCallbackException | IOException e) {
      throw new MachineShellClientException(e);
    }
  }
}
