package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellAuthorizedUsersTest {

  @Mock
  private MachineShellAuthentication authentication;

  private MachineShellAuthorizedUsers authorizedUsers;

  @BeforeEach
  void setUp(
      @Mock MachineShellUserRepository repository,
      @Mock MachineShellAuthenticationConstructor authenticationConstructor) {
    when(authenticationConstructor.construct(repository)).thenReturn(authentication);
    authorizedUsers = new MachineShellAuthorizedUsers(repository, authenticationConstructor);
  }

  @Test
  void handleName(@Mock NameCallback someCallback) throws Exception {
    authorizedUsers.handle(new Callback[] {someCallback});

    verify(authentication).handle(someCallback);
  }

  @Test
  void handleIdentity(@Mock MachineShellIdentityCallback someCallback) throws Exception {
    authorizedUsers.handle(new Callback[] {someCallback});

    verify(authentication).handle(someCallback);
  }

  @Test
  void handlePassword(@Mock PasswordCallback someCallback) throws Exception {
    authorizedUsers.handle(new Callback[] {someCallback});

    verify(authentication).handle(someCallback);
  }

  @Test
  void failOnUnsupportedCallback(@Mock Callback unsupportedCallback) {
    UnsupportedCallbackException e = catchThrowableOfType(() -> authorizedUsers.handle(
        new Callback[] {unsupportedCallback}),
        UnsupportedCallbackException.class);

    assertThat(e.getCallback()).isEqualTo(unsupportedCallback);
  }
}
