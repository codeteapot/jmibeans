package com.github.codeteapot.jmibeans.port;

public interface PlatformPort {

  void listen(MachineManager manager) throws InterruptedException;
}
