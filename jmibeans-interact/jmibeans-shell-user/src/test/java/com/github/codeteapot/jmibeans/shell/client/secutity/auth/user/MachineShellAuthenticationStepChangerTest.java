package com.github.codeteapot.jmibeans.shell.client.secutity.auth.user;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineShellAuthenticationStepChangerTest {

  private static final String SOME_USERNAME = "scott";

  @Mock
  private Consumer<MachineShellAuthenticationStep> changeStepAction;

  @Mock
  private MachineShellAuthenticationCredentialsStepConstructor credentialsStepConstructor;

  @Mock
  private MachineShellAuthenticationUnknownUserStepConstructor unknownUserStepConstructor;

  private MachineShellAuthenticationStepChanger stepChanger;

  @BeforeEach
  void setUp() {
    stepChanger = new MachineShellAuthenticationStepChanger(
        changeStepAction,
        credentialsStepConstructor,
        unknownUserStepConstructor);
  }

  @Test
  void credentials(
      @Mock MachineShellPasswordRegistry somePasswordRegistry,
      @Mock MachineShellUser someUser,
      @Mock MachineShellAuthenticationCredentialsStep someCredentialsStep) {
    when(credentialsStepConstructor.construct(
        stepChanger,
        somePasswordRegistry,
        someUser)).thenReturn(someCredentialsStep);

    stepChanger.credentials(somePasswordRegistry, someUser);

    verify(changeStepAction).accept(someCredentialsStep);
  }

  @Test
  void unknownUser(@Mock MachineShellAuthenticationUnknownUserStep someUnknownUserStep) {
    when(unknownUserStepConstructor.construct(
        stepChanger,
        SOME_USERNAME)).thenReturn(someUnknownUserStep);

    stepChanger.unknownUser(SOME_USERNAME);

    verify(changeStepAction).accept(someUnknownUserStep);
  }
}
