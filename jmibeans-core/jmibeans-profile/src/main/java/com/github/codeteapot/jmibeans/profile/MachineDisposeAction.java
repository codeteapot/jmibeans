package com.github.codeteapot.jmibeans.profile;

@FunctionalInterface
public interface MachineDisposeAction {

  void dispose() throws Exception;
}
