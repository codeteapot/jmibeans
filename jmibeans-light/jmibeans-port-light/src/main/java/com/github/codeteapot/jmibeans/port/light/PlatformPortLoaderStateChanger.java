package com.github.codeteapot.jmibeans.port.light;

import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;

class PlatformPortLoaderStateChanger {

  private final Consumer<PlatformPortLoaderState> changeStateAction;

  PlatformPortLoaderStateChanger(Consumer<PlatformPortLoaderState> changeStateAction) {
    this.changeStateAction = requireNonNull(changeStateAction);
  }

  void loaded(PlatformPortFactory factory) {
    changeStateAction.accept(new PlatformPortLoaderLoadedState(this, factory));
  }
}
