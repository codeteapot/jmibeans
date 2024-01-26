package com.github.codeteapot.jmibeans.shell.client.security.auth.callback;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import java.net.InetAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellHostCallbackTest {

  private static final InetAddress ANY_ADDRESS = getByName("0.0.0.1");

  private static final InetAddress SOME_ADDRESS = getByName("0.0.0.1");
  private static final MachineShellHostStatus SOME_STATUS = KNOWN;

  @Test
  void hasAddress(@Mock MachineShellHostKey anyKey) {
    MachineShellHostCallback callback = new MachineShellHostCallback(SOME_ADDRESS, anyKey);

    InetAddress address = callback.getAddress();

    assertThat(address).isEqualTo(SOME_ADDRESS);
  }

  @Test
  void hasKey(@Mock MachineShellHostKey someKey) {
    MachineShellHostCallback callback = new MachineShellHostCallback(ANY_ADDRESS, someKey);

    MachineShellHostKey key = callback.getKey();

    assertThat(key).isEqualTo(someKey);
  }

  @Test
  void hasStatus(@Mock MachineShellHostKey anyKey) {
    MachineShellHostCallback callback = new MachineShellHostCallback(ANY_ADDRESS, anyKey);
    callback.status = SOME_STATUS;

    MachineShellHostStatus status = callback.getStatus();

    assertThat(status).isEqualTo(SOME_STATUS);
  }

  @Test
  void hasModifiableStatus(@Mock MachineShellHostKey anyKey) {
    MachineShellHostCallback callback = new MachineShellHostCallback(ANY_ADDRESS, anyKey);

    callback.setStatus(SOME_STATUS);

    assertThat(callback.status).isEqualTo(SOME_STATUS);
  }
}
