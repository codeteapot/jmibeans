package com.github.codeteapot.jmibeans.shell.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JschMachineShellHostKeyTest {

  private static final byte[] SOME_ENCODED = {1, 2, 3};

  @Test
  void hasEncoded() {
    JschMachineShellHostKey hostKey = new JschMachineShellHostKey(SOME_ENCODED);

    byte[] encoded = hostKey.getEncoded();

    assertThat(encoded).isEqualTo(SOME_ENCODED);
  }
}
