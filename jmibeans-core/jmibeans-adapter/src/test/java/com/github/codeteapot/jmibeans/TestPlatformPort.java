package com.github.codeteapot.jmibeans;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import com.github.codeteapot.jmibeans.port.MachineLink;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.codeteapot.jmibeans.port.PlatformPort;

class TestPlatformPort implements PlatformPort {

  private BlockingQueue<Consumer<MachineManager>> actions;

  TestPlatformPort() {
    actions = new LinkedBlockingQueue<>();
  }

  void accept(byte[] machineId, MachineLink link) {
    actions.offer(manager -> manager.accept(machineId, link));
  }

  void forget(byte[] machineId) {
    actions.offer(manager -> manager.forget(machineId));
  }

  @Override
  public void listen(MachineManager manager) throws InterruptedException {
    while (true) {
      actions.take().accept(manager);
    }
  }
}
