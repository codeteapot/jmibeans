package com.github.codeteapot.jmibeans;

import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MachineContainerActionPerformer {

  private final Executor actionExecutor;
  private final Map<MachineRef, Lock> actionLockMap;

  MachineContainerActionPerformer() {
    this(newCachedThreadPool());
  }

  MachineContainerActionPerformer(Executor actionExecutor) {
    this.actionExecutor = requireNonNull(actionExecutor);
    actionLockMap = new HashMap<>();
  }

  void perform(MachineRef machineRef, MachineContainerAction action) {
    actionExecutor.execute(() -> {
      Lock actionLock = actionLockMap.computeIfAbsent(machineRef, this::newLock);
      try {
        actionLock.lock();
        action.perform();
      } catch (InterruptedException e) {
        currentThread().interrupt();
      } finally {
        actionLockMap.remove(machineRef);
        actionLock.unlock();
      }
    });
  }

  private Lock newLock(MachineRef machineRef) {
    return new ReentrantLock();
  }
}
