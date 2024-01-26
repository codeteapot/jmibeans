package com.github.codeteapot.jmibeans.shell.client.security.auth.host;

import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefinedDefinedMachineShellHostStoreTest {

  private static final InetAddress SOME_ADDRESS = getByName("0.0.0.1");

  @Test
  void defineDefaultGet(
      @Mock MachineShellHost someHost,
      @Mock Function<InetAddress, Optional<MachineShellHost>> someGet,
      @Mock Consumer<MachineShellHost> anyAdd) {
    when(someGet.apply(SOME_ADDRESS)).thenReturn(Optional.of(someHost));
    DefinedMachineShellHostStore store = new DefinedMachineShellHostStore(someGet, anyAdd);

    Optional<MachineShellHost> host = store.get(SOME_ADDRESS);

    assertThat(host).hasValue(someHost);
  }

  @Test
  void defineDefaultAdd(
      @Mock MachineShellHost someHost,
      @Mock Function<InetAddress, Optional<MachineShellHost>> anyGet,
      @Mock Consumer<MachineShellHost> someAdd) {
    DefinedMachineShellHostStore store = new DefinedMachineShellHostStore(anyGet, someAdd);

    store.add(someHost);

    verify(someAdd).accept(someHost);
  }

  @Test
  void defineMapLikeGet(
      @Mock MachineShellHost someHost,
      @Mock Function<InetAddress, MachineShellHost> someNullableGet,
      @Mock BiConsumer<InetAddress, MachineShellHost> anyPut) {
    when(someNullableGet.apply(SOME_ADDRESS)).thenReturn(someHost);
    DefinedMachineShellHostStore store = new DefinedMachineShellHostStore(someNullableGet, anyPut);

    Optional<MachineShellHost> host = store.get(SOME_ADDRESS);

    assertThat(host).hasValue(someHost);
  }

  @Test
  void defineMapLikeAdd(
      @Mock MachineShellHost someHost,
      @Mock Function<InetAddress, MachineShellHost> anyNullableGet,
      @Mock BiConsumer<InetAddress, MachineShellHost> somePut) {
    when(someHost.getAddress()).thenReturn(SOME_ADDRESS);
    DefinedMachineShellHostStore store = new DefinedMachineShellHostStore(anyNullableGet, somePut);

    store.add(someHost);

    verify(somePut).accept(SOME_ADDRESS, someHost);
  }
}
