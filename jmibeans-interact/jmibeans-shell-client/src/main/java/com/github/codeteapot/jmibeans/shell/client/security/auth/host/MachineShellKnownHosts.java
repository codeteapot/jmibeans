package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.CHANGED;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostStatus;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

public class MachineShellKnownHosts implements CallbackHandler {

  private final Predicate<MachineShellHost> allowed;
  private final MachineShellHostStore store;
  private final BiPredicate<MachineShellHostKey, MachineShellHostKey> sameKey;

  /**
   * <pre>
   * Map&lt;InetAddress, MachineShellHost&gt; hostMap = new HashMap&lt;&gt;();
   * MachineShellKnownHosts knownHosts = new MachineShellKnownHosts(
   *     anyHost -&gt; true,
   *     MachineShellHostStore.define(hostMap::get, hostMap::put));
   * </pre>
   *
   * @param allowed
   * @param store
   */
  public MachineShellKnownHosts(Predicate<MachineShellHost> allowed, MachineShellHostStore store) {
    this(allowed, store, MachineShellKnownHosts::defaultSameKey);
  }

  MachineShellKnownHosts(
      Predicate<MachineShellHost> allowed,
      MachineShellHostStore store,
      BiPredicate<MachineShellHostKey, MachineShellHostKey> sameKey) {
    this.allowed = requireNonNull(allowed);
    this.store = requireNonNull(store);
    this.sameKey = requireNonNull(sameKey);
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (Callback callback : callbacks) {
      if (callback instanceof MachineShellHostCallback) {
        handle((MachineShellHostCallback) callback);
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }

  private void handle(MachineShellHostCallback callback) {
    callback.setStatus(resolve(callback));
  }

  private MachineShellHostStatus resolve(MachineShellHostCallback callback) {
    return store.get(callback.getAddress())
        .map(host -> !sameKey.test(host.getKey(), callback.getKey())
            ? resolve(host, CHANGED)
            : KNOWN)
        .orElseGet(() -> resolve(new CallbackMachineShellHost(callback), KNOWN));
  }

  private MachineShellHostStatus resolve(MachineShellHost host, MachineShellHostStatus newStatus) {
    if (allowed.test(host)) {
      store.add(host);
      return newStatus;
    }
    return UNKNOWN;
  }

  private static boolean defaultSameKey(MachineShellHostKey first, MachineShellHostKey second) {
    return first.equals(second) || Arrays.equals(first.getEncoded(), second.getEncoded());
  }
}
