package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import java.util.Optional;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellAuthenticationTest {

  private static final String ANY_USERNAME = "nobody";

  private static final String SOME_USERNAME = "admin";
  private static final String SOME_INFERRED_USERNAME = "scott";

  private static final MachineShellIdentityName SOME_IDENTITY_NAME = new MachineShellIdentityName(
      "some-identity");
  private static final MachineShellPasswordName SOME_PASSWORD_NAME = new MachineShellPasswordName(
      "some-password");

  private static final char[] SOME_PASSWORD = {'1', '2', '3', '4'};


  @Test
  void initialSetKnownInferredName(
      @Mock MachineShellAuthenticationStepChanger someStepChanger,
      @Mock MachineShellUserRepository someRepository,
      @Mock MachineShellUser someUser,
      @Mock MachineShellPasswordRegistry somePasswordRegistry,
      @Mock NameCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            someStepChanger,
            someRepository));
    when(someRepository.getUser(SOME_USERNAME)).thenReturn(Optional.of(someUser));
    when(someRepository.getPasswordRegistry()).thenReturn(somePasswordRegistry);
    when(someUser.getRemoteName()).thenReturn(Optional.of(SOME_INFERRED_USERNAME));
    when(someCallback.getName()).thenReturn(SOME_USERNAME);

    authentication.handle(someCallback);

    verify(someCallback).setName(SOME_INFERRED_USERNAME);
    verify(someStepChanger).credentials(somePasswordRegistry, someUser);
  }

  @Test
  void initialSetKnownName(
      @Mock MachineShellAuthenticationStepChanger someStepChanger,
      @Mock MachineShellUserRepository someRepository,
      @Mock MachineShellUser someUser,
      @Mock MachineShellPasswordRegistry somePasswordRegistry,
      @Mock NameCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            someStepChanger,
            someRepository));
    when(someRepository.getUser(SOME_USERNAME)).thenReturn(Optional.of(someUser));
    when(someRepository.getPasswordRegistry()).thenReturn(somePasswordRegistry);
    when(someUser.getRemoteName()).thenReturn(Optional.empty());
    when(someCallback.getName()).thenReturn(SOME_USERNAME);

    authentication.handle(someCallback);

    verify(someCallback).setName(SOME_USERNAME);
    verify(someStepChanger).credentials(somePasswordRegistry, someUser);
  }

  @Test
  void initialUnknownName(
      @Mock MachineShellAuthenticationStepChanger someStepChanger,
      @Mock MachineShellUserRepository someRepository,
      @Mock NameCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            someStepChanger,
            someRepository));
    when(someRepository.getUser(SOME_USERNAME)).thenReturn(Optional.empty());
    when(someCallback.getName()).thenReturn(SOME_USERNAME);

    authentication.handle(someCallback);

    verify(someCallback).setName(SOME_USERNAME);
    verify(someStepChanger).unknownUser(SOME_USERNAME);
  }

  @Test
  void failOnCredentialsSetName(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellPasswordRegistry anyPasswordRegistry,
      @Mock MachineShellUser anyUser,
      @Mock NameCallback anyCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            anyPasswordRegistry,
            anyUser));

    Throwable e = catchThrowable(() -> authentication.handle(anyCallback));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failOnUnknownUserSetName(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellPasswordRegistry anyPasswordRegistry,
      @Mock MachineShellUser anyUser,
      @Mock NameCallback anyCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationUnknownUserStep(
            anyStepChanger,
            ANY_USERNAME));

    Throwable e = catchThrowable(() -> authentication.handle(anyCallback));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failOnInitialGetIdentityOnly(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellUserRepository anyRepository) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            anyStepChanger,
            anyRepository));

    Throwable e = catchThrowable(() -> authentication.handle(new MachineShellIdentityCallback()));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void credentialsGetIdentityOnly(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellPasswordRegistry anyPasswordRegistry,
      @Mock MachineShellUser someUser,
      @Mock MachineShellIdentityCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            anyPasswordRegistry,
            someUser));
    when(someUser.getIdentityOnly()).thenReturn(Optional.of(SOME_IDENTITY_NAME));

    authentication.handle(someCallback);

    verify(someCallback).setIdentityOnly(SOME_IDENTITY_NAME);
  }

  @Test
  void unknownUserHasNoIdentityOnly(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellIdentityCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationUnknownUserStep(
            anyStepChanger,
            ANY_USERNAME));

    authentication.handle(someCallback);

    verify(someCallback, never()).setIdentityOnly(any());
  }

  @Test
  void failOnInitialGetPassword(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellUserRepository anyRepository,
      @Mock PasswordCallback anyCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            anyStepChanger,
            anyRepository));

    Throwable e = catchThrowable(() -> authentication.handle(anyCallback));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void credentialsGetPasswordFound(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellPasswordRegistry somePasswordRegistry,
      @Mock MachineShellUser someUser,
      @Mock PasswordCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            somePasswordRegistry,
            someUser));
    when(someUser.getPassword()).thenReturn(Optional.of(SOME_PASSWORD_NAME));
    when(somePasswordRegistry.getPassword(SOME_PASSWORD_NAME))
        .thenReturn(Optional.of(SOME_PASSWORD));

    authentication.handle(someCallback);

    verify(someCallback).setPassword(SOME_PASSWORD);
  }

  @Test
  void credentialsGetPasswordNotFound(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock MachineShellPasswordRegistry somePasswordRegistry,
      @Mock MachineShellUser someUser,
      @Mock PasswordCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            somePasswordRegistry,
            someUser));
    when(someUser.getPassword()).thenReturn(Optional.of(SOME_PASSWORD_NAME));
    when(somePasswordRegistry.getPassword(SOME_PASSWORD_NAME)).thenReturn(Optional.empty());

    authentication.handle(someCallback);

    verify(someCallback, never()).setPassword(any());
  }

  @Test
  void unknownUserHasNoPassword(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock PasswordCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationUnknownUserStep(
            anyStepChanger,
            ANY_USERNAME));

    authentication.handle(someCallback);

    verify(someCallback, never()).setPassword(any());
  }
}
