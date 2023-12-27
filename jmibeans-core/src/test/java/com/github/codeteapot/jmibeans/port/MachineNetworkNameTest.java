package com.github.codeteapot.jmibeans.port;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.port.MachineNetworkName;
import org.junit.jupiter.api.Test;

public class MachineNetworkNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final String ANOTHER_VALUE = "another-value";

  private static final int SOME_HASH_CODE_BY_VALUE = 1943409496;

  @Test
  public void hasValue() {
    MachineNetworkName networkName = new MachineNetworkName(SOME_VALUE);

    String value = networkName.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  public void hashCodeByValue() {
    MachineNetworkName networkName = new MachineNetworkName(SOME_VALUE);

    int hashCode = networkName.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  public void equalsByJavaObjectReference() {
    MachineNetworkName networkName = new MachineNetworkName(ANY_VALUE);
    MachineNetworkName anotherNetworkName = networkName;

    boolean equals = networkName.equals(anotherNetworkName);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByValue() {
    MachineNetworkName networkName = new MachineNetworkName(SOME_VALUE);
    MachineNetworkName anotherNetworkName = new MachineNetworkName(SOME_VALUE);

    boolean equals = networkName.equals(anotherNetworkName);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType() {
    MachineNetworkName networkName = new MachineNetworkName(ANY_VALUE);
    Object anotherObject = new Object();

    boolean equals = networkName.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByValue() {
    MachineNetworkName networkName = new MachineNetworkName(SOME_VALUE);
    MachineNetworkName anotherNetworkName = new MachineNetworkName(ANOTHER_VALUE);

    boolean equals = networkName.equals(anotherNetworkName);

    assertThat(equals).isFalse();
  }

  @Test
  public void toStringValueItself() {
    MachineNetworkName networkName = new MachineNetworkName(SOME_VALUE);

    String str = networkName.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
