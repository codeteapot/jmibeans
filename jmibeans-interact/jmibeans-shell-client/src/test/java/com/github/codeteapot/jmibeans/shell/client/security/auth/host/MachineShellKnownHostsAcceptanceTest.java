package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.CHANGED;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.net.InetAddress;
import javax.security.auth.callback.Callback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellKnownHostsAcceptanceTest {

  private static final InetAddress SOME_ADDRESS = getByName("0.0.0.1");

  private static final byte[] SOME_KEY_ENCODED = {1, 2, 3};
  private static final byte[] ANOTHER_KEY_ENCODED = {4, 5, 6};

  private MachineShellKnownHosts knownHosts;

  @BeforeEach
  void setUp() {
    knownHosts = new MachineShellKnownHosts(anyHost -> true, new TestMachineShellHostStore());
  }

  @Test
  void sameKey(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHostCallback anotherCallback,
      @Mock MachineShellHostKey someKey) throws Exception {
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(someKey);
    when(anotherCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(anotherCallback.getKey()).thenReturn(someKey);

    knownHosts.handle(new Callback[] {someCallback});
    knownHosts.handle(new Callback[] {anotherCallback});

    verify(anotherCallback).setStatus(KNOWN);
  }

  @Test
  void sameKeyEncoded(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHostCallback anotherCallback,
      @Mock MachineShellHostKey someKey,
      @Mock MachineShellHostKey anotherKey) throws Exception {
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(someKey);
    when(anotherCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(anotherCallback.getKey()).thenReturn(anotherKey);
    when(someKey.getEncoded()).thenReturn(SOME_KEY_ENCODED);
    when(anotherKey.getEncoded()).thenReturn(SOME_KEY_ENCODED);

    knownHosts.handle(new Callback[] {someCallback});
    knownHosts.handle(new Callback[] {anotherCallback});

    verify(anotherCallback).setStatus(KNOWN);
  }

  @Test
  void notSameKey(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHostCallback anotherCallback,
      @Mock MachineShellHostKey someKey,
      @Mock MachineShellHostKey anotherKey) throws Exception {
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(someKey);
    when(anotherCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(anotherCallback.getKey()).thenReturn(anotherKey);
    when(someKey.getEncoded()).thenReturn(SOME_KEY_ENCODED);
    when(anotherKey.getEncoded()).thenReturn(ANOTHER_KEY_ENCODED);

    knownHosts.handle(new Callback[] {someCallback});
    knownHosts.handle(new Callback[] {anotherCallback});

    verify(anotherCallback).setStatus(CHANGED);
  }
}
