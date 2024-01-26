package com.github.codeteapot.jmibeans.machine;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.machine.UnknownUserException;
import org.junit.jupiter.api.Test;

public class UnknownUserExceptionTest {

  private static final MachineRef ANY_MACHINE_REF = new MachineRef(new byte[0], new byte[0]);
  private static final String ANY_USERNAME = "any-username";

  private static final MachineRef SOME_MACHINE_REF = new MachineRef(
      new byte[] {0x0a},
      new byte[] {0x01});
  private static final String SOME_USERNAME = "some-username";

  @Test
  public void hasMachineRef() {
    UnknownUserException exception = new UnknownUserException(SOME_MACHINE_REF, ANY_USERNAME);

    MachineRef machineRef = exception.getMachineRef();

    assertThat(machineRef).isEqualTo(SOME_MACHINE_REF);
  }

  @Test
  public void hasUsername() {
    UnknownUserException exception = new UnknownUserException(ANY_MACHINE_REF, SOME_USERNAME);

    String username = exception.getUsername();

    assertThat(username).isEqualTo(SOME_USERNAME);
  }
}
