package com.github.codeteapot.jmibeans.shell.mutable;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import java.net.InetAddress;
import java.util.function.Consumer;
import java.util.function.Function;

public class MachineShellConnectionFactoryLifecycle<F extends MachineShellConnectionFactory> {

  private final Function<InetAddress, F> createMapper;
  private final Consumer<F> disposeAction;

  public MachineShellConnectionFactoryLifecycle(
      Function<InetAddress, F> createMapper,
      Consumer<F> disposeAction) {
    this.createMapper = requireNonNull(createMapper);
    this.disposeAction = requireNonNull(disposeAction);
  }

  public MachineShellConnectionFactoryLifecycle(Function<InetAddress, F> createMapper) {
    this(createMapper, MachineShellConnectionFactoryLifecycle::doNothing);
  }

  F create(InetAddress address) {
    return createMapper.apply(address);
  }

  void dispose(F factory) {
    disposeAction.accept(factory);
  }

  private static <F extends MachineShellConnectionFactory> void doNothing(F any) {};
}
