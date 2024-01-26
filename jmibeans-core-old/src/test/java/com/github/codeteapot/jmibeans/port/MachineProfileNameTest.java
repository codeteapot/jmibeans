package com.github.codeteapot.jmibeans.port;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import org.junit.jupiter.api.Test;

public class MachineProfileNameTest {

  private static final String ANY_VALUE = "any-value";

  private static final String SOME_VALUE = "some-value";
  private static final String ANOTHER_VALUE = "another-value";

  private static final int SOME_HASH_CODE_BY_VALUE = 1943409496;

  @Test
  public void hasValue() {
    MachineProfileName profileName = new MachineProfileName(SOME_VALUE);

    String value = profileName.getValue();

    assertThat(value).isEqualTo(SOME_VALUE);
  }

  @Test
  public void hashCodeByValue() {
    MachineProfileName profileName = new MachineProfileName(SOME_VALUE);

    int hashCode = profileName.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HASH_CODE_BY_VALUE);
  }

  @Test
  public void equalsByJavaObjectReference() {
    MachineProfileName profileName = new MachineProfileName(ANY_VALUE);
    MachineProfileName anotherProfileName = profileName;

    boolean equals = profileName.equals(anotherProfileName);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalsByValue() {
    MachineProfileName profileName = new MachineProfileName(SOME_VALUE);
    MachineProfileName anotherProfileName = new MachineProfileName(SOME_VALUE);

    boolean equals = profileName.equals(anotherProfileName);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualsByJavaType() {
    MachineProfileName profileName = new MachineProfileName(ANY_VALUE);
    Object anotherObject = new Object();

    boolean equals = profileName.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualsByValue() {
    MachineProfileName profileName = new MachineProfileName(SOME_VALUE);
    MachineProfileName anotherProfileName = new MachineProfileName(ANOTHER_VALUE);

    boolean equals = profileName.equals(anotherProfileName);

    assertThat(equals).isFalse();
  }

  @Test
  public void toStringValueItself() {
    MachineProfileName profileName = new MachineProfileName(SOME_VALUE);

    String str = profileName.toString();

    assertThat(str).isEqualTo(SOME_VALUE);
  }
}
