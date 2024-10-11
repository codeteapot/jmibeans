package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.util.function.Consumer;

class DomainZoneStateChanger {

  private final Consumer<DomainZoneState> changeStateAction;

  DomainZoneStateChanger(Consumer<DomainZoneState> changeStateAction) {
    this.changeStateAction = requireNonNull(changeStateAction);
  }

  void available(PlatformContext context, String name, MachineRef serverRef) {
    changeStateAction.accept(new DomainZoneAvailableState(this, context, name, serverRef));
  }

  void unavailable(PlatformContext context, String name) {
    changeStateAction.accept(new DomainZoneUnavailableState(this, context, name));
  }
}
