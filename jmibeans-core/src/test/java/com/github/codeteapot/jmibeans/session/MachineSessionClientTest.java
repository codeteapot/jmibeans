package com.github.codeteapot.jmibeans.session;

import static com.github.codeteapot.testing.InetAddressCreator.getLocalHost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionAuthentication;
import com.github.codeteapot.jmibeans.session.MachineSessionClient;
import com.github.codeteapot.jmibeans.session.MachineSessionIdentityName;
import com.github.codeteapot.jmibeans.session.MachineSessionPasswordName;
import com.github.codeteapot.jmibeans.session.SSHCredentialRepository;
import com.github.codeteapot.jmibeans.session.SSHMachineSession;
import com.github.codeteapot.jmibeans.session.SSHMachineSessionConstructor;
import com.jcraft.jsch.JSch;
import java.io.OutputStream;
import java.net.InetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineSessionClientTest {

  private static final InetAddress SOME_HOST = getLocalHost();
  private static final int SOME_PORT = 1000;
  private static final String SOME_USERNAME = "some-username";

  private static final MachineSessionIdentityName SOME_IDENTITY_NAME =
      new MachineSessionIdentityName("some-identity");

  private static final MachineSessionPasswordName SOME_PASSWORD_NAME =
      new MachineSessionPasswordName("some-password");
  private static final byte[] SOME_PASSWORD = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};

  private MachineSessionClient client;

  @Mock
  private JSch jsch;

  @Mock
  private SSHCredentialRepository credentialRepository;

  @Mock
  private SSHMachineSessionConstructor sessionConstructor;

  @BeforeEach
  public void setUp() {
    client = new MachineSessionClient(jsch, credentialRepository, sessionConstructor);
  }

  @Test
  public void createSession(
      @Mock MachineSessionAuthentication someAuthentication,
      @Mock SSHMachineSession someSession) {
    when(sessionConstructor.construct(
        any(),
        anyLong(),
        eq(jsch),
        eq(SOME_HOST),
        eq(SOME_PORT),
        eq(SOME_USERNAME),
        eq(someAuthentication))).thenReturn(someSession);

    MachineSession session = client.getSession(
        SOME_HOST,
        SOME_PORT,
        SOME_USERNAME,
        someAuthentication);

    assertThat(session).isEqualTo(someSession);
  }

  @Test
  public void generateNamedIdentityKeyPair(@Mock OutputStream someOutput) throws Exception {
    client.generateKeyPair(SOME_IDENTITY_NAME, someOutput);

    verify(credentialRepository).generateKeyPair(SOME_IDENTITY_NAME, someOutput);
  }

  @Test
  public void generateUnnamedIdentityKeyPair(@Mock OutputStream someOutput) throws Exception {
    client.generateKeyPair(someOutput);

    verify(credentialRepository).generateKeyPair(someOutput);
  }

  @Test
  public void passwordAdding() {
    client.addPassword(SOME_PASSWORD_NAME, SOME_PASSWORD);

    verify(credentialRepository).addPassword(SOME_PASSWORD_NAME, SOME_PASSWORD);
  }
}
