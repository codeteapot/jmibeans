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
class PlatformListenIdTest {

  private static final int ANY_RANDOM_NUMBER = 0;

  private static final byte[] SOME_MACHINE_ID = {1, 2, 3};

  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {19},
      new byte[] {1, 2, 3});

  private static final int SOME_RANDOM_NUMBER = 19;
  private static final int ANOTHER_RANDOM_NUMBER = 21;

  private static final int SOME_HASH_CODE_BY_VALUE = 50;

  @Test
  void isParentOf(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);

    boolean result = listenId.isParentOf(SOME_MACHINE_REF);

    assertThat(result).isTrue();
  }

  @Test
  void isNotParentOf(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(ANOTHER_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);

    boolean result = listenId.isParentOf(SOME_MACHINE_REF);

    assertThat(result).isFalse();
  }

  @Test
  void generateMachineRef(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);

    MachineRef machineRef = listenId.machineRef(SOME_MACHINE_ID);

    assertThat(machineRef).isEqualTo(SOME_MACHINE_REF);
  }

  @Test
  void hashCodeByValue(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);

    int hashCode = listenId.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  void equalsByJavaObjectReference(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);
    PlatformListenId anotherListenId = listenId;

    boolean equals = listenId.equals(anotherListenId);

    assertThat(equals).isTrue();
  }

  @Test
  void equalsByValue(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);
    PlatformListenId anotherListenId = new PlatformListenId(someRandomInt);

    boolean equals = listenId.equals(anotherListenId);

    assertThat(equals).isTrue();
  }

  @Test
  void notEqualsByJavaType(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(ANY_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);
    Object anotherObject = new Object();

    boolean equals = listenId.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  void notEqualsByValue(@Mock Function<Integer, Integer> someRandomInt) {
    when(someRandomInt.apply(anyInt()))
        .thenReturn(SOME_RANDOM_NUMBER, ANOTHER_RANDOM_NUMBER);
    PlatformListenId listenId = new PlatformListenId(someRandomInt);
    PlatformListenId anotherListenId = new PlatformListenId(someRandomInt);

    boolean equals = listenId.equals(anotherListenId);

    assertThat(equals).isFalse();
  }
}
