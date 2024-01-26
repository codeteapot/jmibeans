package com.github.codeteapot.jmibeans.shell.client;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import javax.security.auth.callback.CallbackHandler;

class JschMachineShellClientImplementationContext {

  private final JSch jsch;

  JschMachineShellClientImplementationContext(CallbackHandler callbackHandler) {
    jsch = new JSch();
    jsch.setHostKeyRepository(new JschCallbackHandlerHostKeyRepository(callbackHandler));
  }

  KeyPair genKeyPair(int keyType, int keySize) throws JSchException {
    return KeyPair.genKeyPair(jsch, keyType, keySize);
  }

  JschSessionMapper sessionWithCustomPort(int port) {
    return (username, host) -> jsch.getSession(username, host, port);
  }

  JschSessionMapper sessionWithDefaultPort() {
    return (username, host) -> jsch.getSession(username, host);
  }
}
