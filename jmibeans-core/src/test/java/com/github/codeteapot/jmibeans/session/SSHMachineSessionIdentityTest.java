package com.github.codeteapot.jmibeans.session;

import static java.lang.System.getProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.session.SSHMachineSessionIdentity;
import com.github.codeteapot.jmibeans.session.TempFileCreator;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SSHMachineSessionIdentityTest {

  private static final JSchException SOME_ADD_IDENTITY_EXCEPTION = new JSchException();

  @Mock
  private KeyPair keyPair;

  @Mock
  private OutputStream publicKeyOutput;

  @Mock
  private File publicKeyFile;

  private SSHMachineSessionIdentity identity;

  @BeforeEach
  public void setUp(
      @Mock TempFileCreator tempFileCreator,
      @TempDir File tempDir) throws Exception {
    publicKeyFile = new File(tempDir, "public-key-file");
    publicKeyFile.deleteOnExit();
    when(tempFileCreator.create(anyString()))
        .thenReturn(publicKeyFile);
    identity = new SSHMachineSessionIdentity(keyPair, publicKeyOutput, tempFileCreator);

    verify(keyPair).writePublicKey(
        eq(publicKeyOutput),
        argThat(comment -> comment.startsWith(getProperty("user.name").concat("@"))));
  }

  @Test
  public void addPublicKey(@Mock JSch someJsch) throws Exception {
    identity.addTo(someJsch);

    verify(someJsch).addIdentity(publicKeyFile.getAbsolutePath());
  }

  @Test
  public void addPublicKeyFailure(@Mock JSch someJsch) throws Exception {
    doThrow(SOME_ADD_IDENTITY_EXCEPTION)
        .when(someJsch).addIdentity(publicKeyFile.getAbsolutePath());

    Throwable e = catchThrowable(() -> identity.addTo(someJsch));

    assertThat(e)
        .isInstanceOf(IOException.class)
        .hasCause(SOME_ADD_IDENTITY_EXCEPTION);
  }
}
