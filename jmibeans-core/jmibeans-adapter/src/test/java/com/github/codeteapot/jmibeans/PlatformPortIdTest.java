package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.codeteapot.jmibeans.platform.MachineRef;

@ExtendWith(MockitoExtension.class)
class PlatformPortIdTest {

  private static final int ANY_RANDOM_NUMBER = 0;

  private static final byte[] SOME_MACHINE_ID = {1, 2, 3};

  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {19},
      new byte[] {1, 2, 3});

  private static final int SOME_RANDOM_NUMBER = 19;
  private static final int ANOTHER_RANDOM_NUMBER = 0x21;

  private static final int SOME_HASH_CODE_BY_VALUE = 19;

  @Test
  void generateMachineRef(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandomInt);

    MachineRef machineRef = portId.machineRef(SOME_MACHINE_ID);

    assertThat(machineRef).isEqualTo(SOME_MACHINE_REF);
  }

  @Test
  void hashCodeByValue(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandomInt);

    int hashCode = portId.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  void equalsByJavaObjectReference(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandomInt);
    PlatformPortId anotherPortId = portId;

    boolean equals = portId.equals(anotherPortId);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByValue(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandomInt);
    PlatformPortId anotherPortId = new PlatformPortId(someRandomInt);

    boolean equals = portId.equals(anotherPortId);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualsByJavaType(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(ANY_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandomInt);
    Object anotherObject = new Object();

    boolean equals = portId.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualsByValue(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER, ANOTHER_RANDOM_NUMBER);
    PlatformPortId portId = new PlatformPortId(someRandomInt);
    PlatformPortId anotherPortId = new PlatformPortId(someRandomInt);

    boolean equals = portId.equals(anotherPortId);

    assertThat(equals).isFalse();
  }
}
