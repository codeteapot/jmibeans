package com.github.codeteapot.jmibeans.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import org.junit.jupiter.api.Test;

public class MachineLostEventTest {

  private static final Object ANY_SOURCE = new Object();

  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {0x0a},
      new byte[] {0x01});

  @Test
  public void hasMachineRef() {
    MachineLostEvent event = new MachineLostEvent(ANY_SOURCE, SOME_MACHINE_REF);

    MachineRef machineRef = event.getMachineRef();

    assertThat(machineRef).isEqualTo(SOME_MACHINE_REF);
  }
}
