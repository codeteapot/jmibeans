package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.CHANGED;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.KNOWN;
import static com.github.codeteapot.jmibeans.shell.client.security.auth.callback //
    .MachineShellHostStatus.UNKNOWN;
import static com.github.codeteapot.testing.net.InetAddressCreator.getByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.MachineShellHostKey;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellHostCallback;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;
import java.net.InetAddress;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JschCallbackHandlerHostKeyRepositoryTest {

  private static final String ANY_HOST = "[0.0.0.0]:0000";
  private static final String ANY_TYPE = "any-type";
  private static final byte[] ANY_KEY = {};

  private static final InetAddress SOME_HOST = getByName("1.1.1.1");

  // TODO Test raw host as well: 1.1.1.1
  private static final String SOME_HOST_LINE = "[1.1.1.1]:1234";
  private static final String UNRECOGNIZED_HOST_LINE = "[1.1.1.1]:abcd";

  private static final byte[] SOME_KEY = {1, 2, 4};

  @Mock
  private CallbackHandler callbackHandler;

  @Mock
  private JschMachineShellHostKeyFactory hostKeyFactory;

  @Mock
  private MachineShellHostCallbackConstructor hostCallbackConstructor;

  private JschCallbackHandlerHostKeyRepository repository;

  @BeforeEach
  void setUp() {
    repository = new JschCallbackHandlerHostKeyRepository(
        callbackHandler,
        hostKeyFactory,
        hostCallbackConstructor);
  }

  @Test
  void getKnownHostsRepositoryIDIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.getKnownHostsRepositoryID());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getHostKeyIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.getHostKey());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getHostKeyWithArgumentsIsNotRelevant() {
    HostKey[] hostKeys = repository.getHostKey(ANY_HOST, ANY_TYPE);

    assertThat(hostKeys).isEmpty();
  }

  @Test
  void checkKnown(
      @Mock MachineShellHostCallback someHostCallback,
      @Mock MachineShellHostKey someHostKey) throws Exception {
    when(hostCallbackConstructor.construct(SOME_HOST, someHostKey))
        .thenReturn(someHostCallback);
    when(hostKeyFactory.getHostKey(SOME_KEY))
        .thenReturn(someHostKey);
    when(someHostCallback.getStatus())
        .thenReturn(KNOWN);

    int result = repository.check(SOME_HOST_LINE, SOME_KEY);

    verify(callbackHandler).handle(new Callback[] {someHostCallback});
    assertThat(result).isEqualTo(HostKeyRepository.OK);
  }

  @Test
  void checkChanged(
      @Mock MachineShellHostCallback someHostCallback,
      @Mock MachineShellHostKey someHostKey) throws Exception {
    when(hostCallbackConstructor.construct(SOME_HOST, someHostKey))
        .thenReturn(someHostCallback);
    when(hostKeyFactory.getHostKey(SOME_KEY))
        .thenReturn(someHostKey);
    when(someHostCallback.getStatus())
        .thenReturn(CHANGED);

    int result = repository.check(SOME_HOST_LINE, SOME_KEY);

    verify(callbackHandler).handle(new Callback[] {someHostCallback});
    assertThat(result).isEqualTo(HostKeyRepository.CHANGED);
  }

  @Test
  void checkUnknown(
      @Mock MachineShellHostCallback someHostCallback,
      @Mock MachineShellHostKey someHostKey) throws Exception {
    when(hostCallbackConstructor.construct(SOME_HOST, someHostKey))
        .thenReturn(someHostCallback);
    when(hostKeyFactory.getHostKey(SOME_KEY))
        .thenReturn(someHostKey);
    when(someHostCallback.getStatus())
        .thenReturn(UNKNOWN);

    int result = repository.check(SOME_HOST_LINE, SOME_KEY);

    verify(callbackHandler).handle(new Callback[] {someHostCallback});
    assertThat(result).isEqualTo(HostKeyRepository.NOT_INCLUDED);
  }

  @Test
  void checkNotRecognized() {
    int result = repository.check(UNRECOGNIZED_HOST_LINE, ANY_KEY);

    assertThat(result).isEqualTo(HostKeyRepository.NOT_INCLUDED);
  }


  @Test
  void addIsNotNeeded(@Mock HostKey anyHostKey, @Mock UserInfo anyUserInfo) {
    Throwable e = catchThrowable(() -> repository.add(anyHostKey, anyUserInfo));

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void removeIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.remove(ANY_HOST, ANY_TYPE));

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void removeWithKeyIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.remove(ANY_HOST, ANY_TYPE, ANY_KEY));

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }
}
