package com.github.codeteapot.jmibeans;

@FunctionalInterface
interface MachineContainerAction {

  void perform() throws InterruptedException;
}
