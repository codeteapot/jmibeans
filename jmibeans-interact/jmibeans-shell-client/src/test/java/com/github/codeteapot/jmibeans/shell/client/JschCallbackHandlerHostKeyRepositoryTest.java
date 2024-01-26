package com.github.codeteapot.jmibeans.shell.client;

import static com.jcraft.jsch.HostKeyRepository.CHANGED;
import static com.jcraft.jsch.HostKeyRepository.NOT_INCLUDED;
import static com.jcraft.jsch.HostKeyRepository.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.UserInfo;

@ExtendWith(MockitoExtension.class)
class JschCallbackHandlerHostKeyRepositoryTest {

  private static final String ANY_HOST = "[0.0.0.0]:0000";
  private static final String ANY_TYPE = "any-type";
  private static final byte[] ANY_KEY = {};

  private static final String SOME_ALLOWED_HOST = "1.1.1.1";
  private static final String SOME_HOST = "[1.1.1.1]:1234";
  private static final String ANOTHER_HOST = "[1.1.1.2]:1234";
  private static final String NOT_RECOGNIZED_HOST = "[1.1.1.1]:abcd";

  private static final byte[] OLD_KEY = {1, 2, 3};
  private static final byte[] NEW_KEY = {4, 5, 6};

  private TestMachineShellClientContextCallbackHandler callbackHandler;

  private JschCallbackHandlerHostKeyRepository repository;

  @BeforeEach
  void setUp() {
    callbackHandler = new TestMachineShellClientContextCallbackHandler();
    repository = new JschCallbackHandlerHostKeyRepository(callbackHandler);
  }

  @AfterEach
  void tearDown() {
    callbackHandler.reset();
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
  void checkKnown() {
    callbackHandler.addAllowedHost(SOME_ALLOWED_HOST);

    int result = repository.check(SOME_HOST, ANY_KEY);

    assertThat(result).isEqualTo(OK);
  }

  @Test
  void checkChanged() {
    callbackHandler.addAllowedHost(SOME_ALLOWED_HOST);
    callbackHandler.addKnownHost(SOME_ALLOWED_HOST, OLD_KEY);

    int result = repository.check(SOME_HOST, NEW_KEY);

    assertThat(result).isEqualTo(CHANGED);
  }

  @Test
  void checkUnknown() {
    callbackHandler.addAllowedHost(SOME_ALLOWED_HOST);

    int result = repository.check(ANOTHER_HOST, ANY_KEY);

    assertThat(result).isEqualTo(NOT_INCLUDED);
  }

  @Test
  void checkNotRecognized() {
    callbackHandler.addAllowedHost(SOME_ALLOWED_HOST);

    int result = repository.check(NOT_RECOGNIZED_HOST, ANY_KEY);

    assertThat(result).isEqualTo(NOT_INCLUDED);
  }

  @Test
  void checkUnsupported() {
    callbackHandler.setSupported(false);

    int result = repository.check(ANY_HOST, ANY_KEY);

    assertThat(result).isEqualTo(NOT_INCLUDED);
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
