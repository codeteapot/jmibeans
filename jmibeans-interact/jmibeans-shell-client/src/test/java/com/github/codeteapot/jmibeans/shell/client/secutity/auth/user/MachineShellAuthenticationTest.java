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
import java.util.function.Function;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellAuthenticationTest {

  private static final String ANY_USERNAME = "nobody";

  private static final MachineShellUser NULL_USER = null;
  private static final char[] NULL_PASSWORD = null;

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
      @Mock Function<String, MachineShellUser> someUserGetter,
      @Mock MachineShellUser someUser,
      @Mock Function<MachineShellPasswordName, char[]> somePasswordGetter,
      @Mock NameCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            someStepChanger,
            someUserGetter,
            somePasswordGetter));
    when(someUserGetter.apply(SOME_USERNAME)).thenReturn(someUser);
    when(someUser.getRemoteName()).thenReturn(SOME_INFERRED_USERNAME);
    when(someCallback.getName()).thenReturn(SOME_USERNAME);

    authentication.handle(someCallback);

    verify(someCallback).setName(SOME_INFERRED_USERNAME);
    verify(someStepChanger).credentials(somePasswordGetter, someUser);
  }

  @Test
  void initialUnknownName(
      @Mock MachineShellAuthenticationStepChanger someStepChanger,
      @Mock Function<String, MachineShellUser> someUserGetter,
      @Mock Function<MachineShellPasswordName, char[]> anyPasswordGetter,
      @Mock NameCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            someStepChanger,
            someUserGetter,
            anyPasswordGetter));
    when(someUserGetter.apply(SOME_USERNAME)).thenReturn(NULL_USER);
    when(someCallback.getName()).thenReturn(SOME_USERNAME);

    authentication.handle(someCallback);

    verify(someCallback).setName(SOME_USERNAME);
    verify(someStepChanger).unknownUser(SOME_USERNAME);
  }

  @Test
  void failOnCredentialsSetName(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock Function<MachineShellPasswordName, char[]> anyPasswordGetter,
      @Mock MachineShellUser anyUser,
      @Mock NameCallback anyCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            anyPasswordGetter,
            anyUser));

    Throwable e = catchThrowable(() -> authentication.handle(anyCallback));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void failOnUnknownUserSetName(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
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
      @Mock Function<String, MachineShellUser> anyUserGetter,
      @Mock Function<MachineShellPasswordName, char[]> anyPasswordGetter) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            anyStepChanger,
            anyUserGetter,
            anyPasswordGetter));

    Throwable e = catchThrowable(() -> authentication.handle(new MachineShellIdentityCallback()));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void credentialsGetIdentityOnly(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock Function<MachineShellPasswordName, char[]> anyPasswordGetter,
      @Mock MachineShellUser someUser,
      @Mock MachineShellIdentityCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            anyPasswordGetter,
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
      @Mock Function<String, MachineShellUser> anyUserGetter,
      @Mock Function<MachineShellPasswordName, char[]> anyPasswordGetter,
      @Mock PasswordCallback anyCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationInitialStep(
            anyStepChanger,
            anyUserGetter,
            anyPasswordGetter));

    Throwable e = catchThrowable(() -> authentication.handle(anyCallback));

    assertThat(e).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void credentialsGetPasswordFound(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock Function<MachineShellPasswordName, char[]> somePasswordGetter,
      @Mock MachineShellUser someUser,
      @Mock PasswordCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            somePasswordGetter,
            someUser));
    when(someUser.getPasswordName()).thenReturn(Optional.of(SOME_PASSWORD_NAME));
    when(somePasswordGetter.apply(SOME_PASSWORD_NAME)).thenReturn(SOME_PASSWORD);

    authentication.handle(someCallback);

    verify(someCallback).setPassword(SOME_PASSWORD);
  }

  @Test
  void credentialsGetPasswordNotFound(
      @Mock MachineShellAuthenticationStepChanger anyStepChanger,
      @Mock Function<MachineShellPasswordName, char[]> somePasswordGetter,
      @Mock MachineShellUser someUser,
      @Mock PasswordCallback someCallback) {
    MachineShellAuthentication authentication = new MachineShellAuthentication(
        changeStepAction -> new MachineShellAuthenticationCredentialsStep(
            anyStepChanger,
            somePasswordGetter,
            someUser));
    when(someUser.getPasswordName()).thenReturn(Optional.of(SOME_PASSWORD_NAME));
    when(somePasswordGetter.apply(SOME_PASSWORD_NAME)).thenReturn(NULL_PASSWORD);

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
