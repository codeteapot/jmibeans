package com.github.codeteapot.jmibeans.library.dns;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import java.util.function.Consumer;

class DNSZoneStateChanger {

  private final Consumer<DNSZoneState> changeStateAction;

  DNSZoneStateChanger(Consumer<DNSZoneState> changeStateAction) {
    this.changeStateAction = requireNonNull(changeStateAction);
  }

  void available(PlatformContext context, String name, MachineRef serverRef) {
    changeStateAction.accept(new DNSZoneAvailableState(this, context, name, serverRef));
  }

  void unavailable(PlatformContext context, String name) {
    changeStateAction.accept(new DNSZoneUnavailableState(this, context, name));
  }
}
