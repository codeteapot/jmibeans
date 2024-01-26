package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.CHANGED;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostStatus;

public class MachineShellKnownHosts implements CallbackHandler {

  private final Predicate<MachineShellHost> allowed;
  private final Function<InetAddress, MachineShellHost> getter;
  private final Consumer<MachineShellHost> adder;
  private final Comparator<MachineShellHostKey> keyComparator;

  public MachineShellKnownHosts(
      Predicate<MachineShellHost> allowed,
      Function<InetAddress, MachineShellHost> getter,
      Consumer<MachineShellHost> adder) {
    this(allowed, getter, adder, defaultKeyComparator());
  }

  public MachineShellKnownHosts(
      Predicate<MachineShellHost> allowed,
      Function<InetAddress, MachineShellHost> getter,
      BiConsumer<InetAddress, MachineShellHost> putter) {
    this(allowed, getter, host -> putter.accept(host.getAddress(), host), defaultKeyComparator());
  }

  MachineShellKnownHosts(
      Predicate<MachineShellHost> allowed,
      Function<InetAddress, MachineShellHost> getter,
      Consumer<MachineShellHost> adder,
      Comparator<MachineShellHostKey> keyComparator) {
    this.allowed = requireNonNull(allowed);
    this.getter = requireNonNull(getter);
    this.adder = requireNonNull(adder);
    this.keyComparator = requireNonNull(keyComparator);
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
    callback.setStatus(ofNullable(getter.apply(callback.getAddress()))
        .map(host -> keyComparator.compare(host.getKey(), callback.getKey()) != 0
            ? resolve(host, CHANGED)
            : KNOWN)
        .orElseGet(() -> resolve(new CallbackMachineShellHost(callback), KNOWN)));
  }

  private MachineShellHostStatus resolve(MachineShellHost host, MachineShellHostStatus newStatus) {
    if (allowed.test(host)) {
      adder.accept(host);
      return newStatus;
    }
    return UNKNOWN;
  }

  private static Comparator<MachineShellHostKey> defaultKeyComparator() {
    return (first, second) -> first.equals(second) || Arrays.equals(
        first.getEncoded(),
        second.getEncoded()) ? 0 : -1;
  }
}
