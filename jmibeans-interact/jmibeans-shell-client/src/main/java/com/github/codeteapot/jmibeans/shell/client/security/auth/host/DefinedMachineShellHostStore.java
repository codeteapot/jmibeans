package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.net.InetAddress;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO DESIGN Review this interface
public class DefinedMachineShellHostStore implements MachineShellHostStore {

  private final Function<InetAddress, Optional<MachineShellHost>> get;
  private final Consumer<MachineShellHost> add;

  public DefinedMachineShellHostStore(
      Function<InetAddress, Optional<MachineShellHost>> get,
      Consumer<MachineShellHost> add) {
    this.get = requireNonNull(get);
    this.add = requireNonNull(add);
  }

  public DefinedMachineShellHostStore(
      Function<InetAddress, MachineShellHost> nullableGet,
      BiConsumer<InetAddress, MachineShellHost> put) {
    this(
        address -> ofNullable(nullableGet.apply(address)),
        host -> put.accept(host.getAddress(), host));
  }

  @Override
  public Optional<MachineShellHost> get(InetAddress address) {
    return get.apply(address);
  }

  @Override
  public void add(MachineShellHost host) {
    add.accept(host);
  }
}
