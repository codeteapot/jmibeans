package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.port.MachineId;
import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

class TestPlatformPort implements PlatformPort {

  private BlockingQueue<Consumer<MachineManager>> actions;

  TestPlatformPort() {
    actions = new LinkedBlockingQueue<>();
  }

  void accept(MachineId id, MachineLink link) {
    actions.offer(manager -> manager.accept(id, link));
  }

  void forget(MachineId id) {
    actions.offer(manager -> manager.forget(id));
  }

  @Override
  public void listen(MachineManager manager) throws InterruptedException {
    while (true) {
      actions.take().accept(manager);
    }
  }
}
