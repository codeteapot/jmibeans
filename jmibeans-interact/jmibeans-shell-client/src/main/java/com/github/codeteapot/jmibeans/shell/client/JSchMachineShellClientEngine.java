package com.github.codeteapot.jmibeans.shell.client;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import javax.security.auth.callback.CallbackHandler;

class JSchMachineShellClientEngine {

  private final JSch jsch;

  JSchMachineShellClientEngine(CallbackHandler callbackHandler) {
    jsch = new JSch();
    jsch.setHostKeyRepository(new JSchCallbackHandlerHostKeyRepository(callbackHandler));
  }

  KeyPair genKeyPair(int keyType, int keySize) throws JSchException {
    return KeyPair.genKeyPair(jsch, keyType, keySize);
  }

  JSchSessionMapper sessionWithCustomPort(int port) {
    return (username, host) -> jsch.getSession(username, host, port);
  }

  JSchSessionMapper sessionWithDefaultPort() {
    return (username, host) -> jsch.getSession(username, host);
  }
}
