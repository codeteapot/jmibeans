package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.PlatformPortId;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.port.MachineId;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlatformPortIdTest {

  private static final int ANY_RANDOM_NUMBER = 0;

  private static final MachineId SOME_MACHINE_ID = new MachineId(new byte[] {0x01, 0x03});

  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {0x19},
      new byte[] {0x01, 0x03});

  private static final int SOME_RANDOM_NUMBER = 0x19;
  private static final int ANOTHER_RANDOM_NUMBER = 0x21;

  private static final int SOME_HASH_CODE_BY_VALUE = 5;

  @Test
  public void generateMachineRef(@Mock Random someRandom) {
    when(someRandom.nextInt(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandom);

    MachineRef machineRef = portId.machineRef(SOME_MACHINE_ID);

    assertThat(machineRef).isEqualTo(SOME_MACHINE_REF);
  }

  @Test
  public void hashCodeByValue(@Mock Random someRandom) {
    when(someRandom.nextInt(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandom);

    int hashCode = portId.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  public void equalsByJavaObjectReference(@Mock Random someRandom) {
    when(someRandom.nextInt(anyInt()))
        .thenReturn(ANY_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandom);
    PlatformPortId anotherPortId = portId;

    boolean equals = portId.equals(anotherPortId);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByValue(@Mock Random someRandom) {
    when(someRandom.nextInt(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandom);
    PlatformPortId anotherPortId = new PlatformPortId(someRandom);

    boolean equals = portId.equals(anotherPortId);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType(@Mock Random someRandom) {
    when(someRandom.nextInt(anyInt()))
        .thenReturn(ANY_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandom);
    Object anotherObject = new Object();

    boolean equals = portId.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByValue(@Mock Random someRandom) {
    when(someRandom.nextInt(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER, ANOTHER_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandom);
    PlatformPortId anotherPortId = new PlatformPortId(someRandom);

    boolean equals = portId.equals(anotherPortId);


    assertThat(equals).isFalse();
  }
}
