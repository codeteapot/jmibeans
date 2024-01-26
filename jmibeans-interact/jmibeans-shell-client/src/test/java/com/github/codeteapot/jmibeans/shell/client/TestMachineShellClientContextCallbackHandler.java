package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellHostStatus.CHANGED;
import static com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static java.net.InetAddress.getByName;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellHostCallback;

class TestMachineShellClientContextCallbackHandler implements CallbackHandler {

  private final Set<InetAddress> allowedHosts;
  private final Map<InetAddress, byte[]> knownHosts;
  private boolean supported;

  TestMachineShellClientContextCallbackHandler() {
    allowedHosts = new HashSet<>();
    knownHosts = new HashMap<>();
    supported = true;
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (Callback callback : callbacks) {
      if (supported && callback instanceof MachineShellHostCallback) {
        handle((MachineShellHostCallback) callback);
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }

  void addAllowedHost(String allowedHost) {
    try {
      allowedHosts.add(getByName(allowedHost));
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException(e);
    }
  }

  void addKnownHost(String knownHost, byte[] key) {
    try {
      knownHosts.put(getByName(knownHost), key);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException(e);
    }
  }

  void setSupported(boolean supported) {
    this.supported = supported;
  }

  void reset() {
    allowedHosts.clear();
    knownHosts.clear();
    supported = true;
  }

  private void handle(MachineShellHostCallback callback) {
    if (allowedHosts.contains(callback.getHost())) {
      byte[] key = knownHosts.get(callback.getHost());
      if (key == null) {
        knownHosts.put(callback.getHost(), callback.getKey());
        callback.setStatus(KNOWN);
      } else {
        if (Arrays.equals(key, callback.getKey())) {
          callback.setStatus(KNOWN);
        } else {
          knownHosts.put(callback.getHost(), key);
          callback.setStatus(CHANGED);
        }
      }
    } else {
      callback.setStatus(UNKNOWN);
    }
  }
}
