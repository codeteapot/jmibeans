package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.callback.MachineShellIdentityCallback;
import java.util.Optional;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellAuthorizedUsersAcceptanceTest {

  private static final String SOME_USERNAME = "admin";
  private static final String SOME_INFERRED_USERNAME = "scott";

  private static final MachineShellIdentityName SOME_IDENTITY_NAME = new MachineShellIdentityName(
      "some-identity");
  private static final MachineShellPasswordName SOME_PASSWORD_NAME = new MachineShellPasswordName(
      "some-password");

  private static final char[] SOME_PASSWORD = {'1', '2', '3', '4'};

  @Mock
  private MachineShellUserRepository repository;

  @Mock
  private MachineShellPasswordRegistry passwordRegistry;

  private MachineShellAuthorizedUsers authorizedUsers;

  @BeforeEach
  void setUp() {
    authorizedUsers = new MachineShellAuthorizedUsers(repository);
    when(repository.getPasswordRegistry()).thenReturn(passwordRegistry);
  }

  @Test
  void fullAuthentication(
      @Mock MachineShellUser someUser,
      @Mock NameCallback someNameCallback,
      @Mock MachineShellIdentityCallback someIdentityCallback,
      @Mock PasswordCallback somePasswordCallback) throws Exception {
    when(repository.getUser(SOME_USERNAME)).thenReturn(Optional.of(someUser));
    when(passwordRegistry.getPassword(SOME_PASSWORD_NAME)).thenReturn(Optional.of(SOME_PASSWORD));
    when(someUser.getRemoteName()).thenReturn(Optional.of(SOME_INFERRED_USERNAME));
    when(someUser.getIdentityOnly()).thenReturn(Optional.of(SOME_IDENTITY_NAME));
    when(someUser.getPassword()).thenReturn(Optional.of(SOME_PASSWORD_NAME));
    when(someNameCallback.getName()).thenReturn(SOME_USERNAME);

    authorizedUsers.handle(new Callback[] {
        someNameCallback,
        someIdentityCallback,
        somePasswordCallback
    });

    verify(someNameCallback).setName(SOME_INFERRED_USERNAME);
    verify(someIdentityCallback).setIdentityOnly(SOME_IDENTITY_NAME);
    verify(somePasswordCallback).setPassword(SOME_PASSWORD);
  }
}
