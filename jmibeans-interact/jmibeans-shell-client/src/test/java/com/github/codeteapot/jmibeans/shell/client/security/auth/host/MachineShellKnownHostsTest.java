package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.CHANGED;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import java.net.InetAddress;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellKnownHostsTest {

  private static final boolean ALLOWED = true;
  private static final boolean NOT_ALLOWED = false;

  private static final boolean SAME_KEY = true;
  private static final boolean NOT_SAME_KEY = false;

  private static final InetAddress SOME_ADDRESS = getByName("0.0.0.1");

  @Mock
  private Predicate<MachineShellHost> allowed;

  @Mock
  private MachineShellHostStore store;

  @Mock
  private BiPredicate<MachineShellHostKey, MachineShellHostKey> sameKey;

  private MachineShellKnownHosts knownHosts;

  @BeforeEach
  void setUp() {
    knownHosts = new MachineShellKnownHosts(allowed, store, sameKey);
  }

  @Test
  void existingKnown(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHost someHost,
      @Mock MachineShellHostKey someKey,
      @Mock MachineShellHostKey anotherKey) throws Exception {
    when(store.get(SOME_ADDRESS)).thenReturn(Optional.of(someHost));
    when(sameKey.test(someKey, anotherKey)).thenReturn(SAME_KEY);
    when(someHost.getKey()).thenReturn(someKey);
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(anotherKey);

    knownHosts.handle(new Callback[] {someCallback});

    verify(someCallback).setStatus(KNOWN);
  }

  @Test
  void existingChangedAllowed(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHost someHost,
      @Mock MachineShellHostKey someKey,
      @Mock MachineShellHostKey anotherKey) throws Exception {
    when(allowed.test(someHost)).thenReturn(ALLOWED);
    when(store.get(SOME_ADDRESS)).thenReturn(Optional.of(someHost));
    when(sameKey.test(someKey, anotherKey)).thenReturn(NOT_SAME_KEY);
    when(someHost.getKey()).thenReturn(someKey);
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(anotherKey);

    knownHosts.handle(new Callback[] {someCallback});

    verify(someCallback).setStatus(CHANGED);
    verify(store).add(someHost);
  }

  @Test
  void existingChangedNotAllowed(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHost someHost,
      @Mock MachineShellHostKey someKey,
      @Mock MachineShellHostKey anotherKey) throws Exception {
    when(allowed.test(someHost)).thenReturn(NOT_ALLOWED);
    when(store.get(SOME_ADDRESS)).thenReturn(Optional.of(someHost));
    when(sameKey.test(someKey, anotherKey)).thenReturn(NOT_SAME_KEY);
    when(someHost.getKey()).thenReturn(someKey);
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(anotherKey);

    knownHosts.handle(new Callback[] {someCallback});

    verify(someCallback).setStatus(UNKNOWN);
    verify(store, never()).add(any());
  }

  @Test
  void nonExistingAllowed(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHostKey someKey) throws Exception {
    when(allowed.test(argThat(host -> SOME_ADDRESS.equals(host.getAddress())
        && someKey.equals(host.getKey())))).thenReturn(ALLOWED);
    when(store.get(SOME_ADDRESS)).thenReturn(Optional.empty());
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(someKey);

    knownHosts.handle(new Callback[] {someCallback});

    verify(someCallback).setStatus(KNOWN);
    verify(store).add(argThat(host -> SOME_ADDRESS.equals(host.getAddress())
        && someKey.equals(host.getKey())));
  }

  @Test
  void nonExistingNotAllowed(
      @Mock MachineShellHostCallback someCallback,
      @Mock MachineShellHostKey someKey) throws Exception {
    when(allowed.test(argThat(host -> SOME_ADDRESS.equals(host.getAddress())
        && someKey.equals(host.getKey())))).thenReturn(NOT_ALLOWED);
    when(store.get(SOME_ADDRESS)).thenReturn(Optional.empty());
    when(someCallback.getAddress()).thenReturn(SOME_ADDRESS);
    when(someCallback.getKey()).thenReturn(someKey);

    knownHosts.handle(new Callback[] {someCallback});

    verify(someCallback).setStatus(UNKNOWN);
    verify(store, never()).add(any());
  }

  @Test
  void failOnUsupportedCallback(@Mock Callback unsupportedCallback) throws Exception {
    UnsupportedCallbackException e = catchThrowableOfType(
        () -> knownHosts.handle(new Callback[] {unsupportedCallback}),
        UnsupportedCallbackException.class);

    assertThat(e.getCallback()).isEqualTo(unsupportedCallback);
  }
}
