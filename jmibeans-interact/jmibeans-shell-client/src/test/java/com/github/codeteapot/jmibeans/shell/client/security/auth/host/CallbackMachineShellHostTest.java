package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.net.InetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallbackMachineShellHostTest {

  private static final InetAddress SOME_ADDRESS = getByName("0.0.0.1");

  @Mock
  private MachineShellHostCallback callback;

  private CallbackMachineShellHost host;

  @BeforeEach
  void setUp() {
    host = new CallbackMachineShellHost(callback);
  }

  @Test
  void hasAddress() {
    when(callback.getAddress()).thenReturn(SOME_ADDRESS);

    InetAddress address = host.getAddress();

    assertThat(address).isEqualTo(SOME_ADDRESS);
  }

  @Test
  void hasKey(@Mock MachineShellHostKey someKey) {
    when(callback.getKey()).thenReturn(someKey);

    MachineShellHostKey key = host.getKey();

    assertThat(key).isEqualTo(someKey);
  }
}
