package com.github.codeteapot.jmibeans.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.session.KeyPairGenerator;
import com.github.codeteapot.jmibeans.session.MachineSessionIdentityName;
import com.github.codeteapot.jmibeans.session.MachineSessionPasswordName;
import com.github.codeteapot.jmibeans.session.SSHCredentialRepository;
import com.github.codeteapot.jmibeans.session.SSHMachineSessionConstructor;
import com.github.codeteapot.jmibeans.session.SSHMachineSessionIdentity;
import com.github.codeteapot.jmibeans.session.SSHMachineSessionIdentityConstructor;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SSHCredentialRepositoryTest {

  private static final MachineSessionIdentityName ANY_IDENTITY_NAME =
      new MachineSessionIdentityName("any-identity");

  private static final byte[] ANY_PASSWORD = {};

  private static final MachineSessionIdentityName SOME_IDENTITY_NAME =
      new MachineSessionIdentityName("some-identity");

  private static final JSchException SOME_KEY_PAIR_GENERATOR_EXCEPTION = new JSchException();

  private static final MachineSessionPasswordName SOME_PASSWORD_NAME =
      new MachineSessionPasswordName("some-password");
  private static final MachineSessionPasswordName ANOTHER_PASSWORD_NAME =
      new MachineSessionPasswordName("another-password");
  private static final byte[] SOME_PASSWORD = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};


  private SSHCredentialRepository repository;

  @Mock
  private JSch jsch;

  private Set<SSHMachineSessionIdentity> unnamedIdentities;

  private Map<MachineSessionIdentityName, SSHMachineSessionIdentity> namedIdentityMap;

  private Map<MachineSessionPasswordName, byte[]> passwordMap;

  @Mock
  private KeyPairGenerator keyPairGenerator;

  @Mock
  private SSHMachineSessionConstructor sessionConstructor;

  @Mock
  private SSHMachineSessionIdentityConstructor sessionIdentityConstructor;

  @BeforeEach
  public void setUp() {
    unnamedIdentities = new HashSet<>();
    namedIdentityMap = new HashMap<>();
    passwordMap = new HashMap<>();
    repository = new SSHCredentialRepository(
        jsch,
        unnamedIdentities,
        namedIdentityMap,
        passwordMap,
        keyPairGenerator,
        sessionIdentityConstructor);
  }

  @Test
  public void generateNamedIdentityKeyPair(
      @Mock KeyPair someKeyPair,
      @Mock SSHMachineSessionIdentity someSessionIdentity,
      @Mock OutputStream someOutput) throws Exception {
    when(keyPairGenerator.generate(eq(jsch), anyInt(), anyInt()))
        .thenReturn(someKeyPair);
    when(sessionIdentityConstructor.construct(eq(someKeyPair), eq(someOutput), any()))
        .thenReturn(someSessionIdentity);

    repository.generateKeyPair(SOME_IDENTITY_NAME, someOutput);

    assertThat(namedIdentityMap).containsEntry(SOME_IDENTITY_NAME, someSessionIdentity);
    verify(someSessionIdentity).addTo(jsch);
  }

  @Test
  public void generateNamedIdentityKeyPairFailure(
      @Mock KeyPair someKeyPair,
      @Mock SSHMachineSessionIdentity someSessionIdentity,
      @Mock OutputStream anyOutput) throws Exception {
    when(keyPairGenerator.generate(eq(jsch), anyInt(), anyInt()))
        .thenThrow(SOME_KEY_PAIR_GENERATOR_EXCEPTION);

    Throwable e = catchThrowable(() -> repository.generateKeyPair(ANY_IDENTITY_NAME, anyOutput));

    assertThat(e)
        .isInstanceOf(IOException.class)
        .hasCause(SOME_KEY_PAIR_GENERATOR_EXCEPTION);
  }

  @Test
  public void generateUnnamedIdentityKeyPair(
      @Mock KeyPair someKeyPair,
      @Mock SSHMachineSessionIdentity someSessionIdentity,
      @Mock OutputStream someOutput) throws Exception {
    when(keyPairGenerator.generate(eq(jsch), anyInt(), anyInt()))
        .thenReturn(someKeyPair);
    when(sessionIdentityConstructor.construct(eq(someKeyPair), eq(someOutput), any()))
        .thenReturn(someSessionIdentity);

    repository.generateKeyPair(someOutput);

    assertThat(unnamedIdentities).contains(someSessionIdentity);
    verify(someSessionIdentity).addTo(jsch);
  }

  @Test
  public void generateUnnamedIdentityKeyPairFailure(
      @Mock KeyPair someKeyPair,
      @Mock SSHMachineSessionIdentity someSessionIdentity,
      @Mock OutputStream anyOutput) throws Exception {
    when(keyPairGenerator.generate(eq(jsch), anyInt(), anyInt()))
        .thenThrow(SOME_KEY_PAIR_GENERATOR_EXCEPTION);

    Throwable e = catchThrowable(() -> repository.generateKeyPair(anyOutput));

    assertThat(e)
        .isInstanceOf(IOException.class)
        .hasCause(SOME_KEY_PAIR_GENERATOR_EXCEPTION);
  }

  @Test
  public void passwordMappingWithValue() {
    passwordMap.put(SOME_PASSWORD_NAME, SOME_PASSWORD);

    Optional<byte[]> password = repository.passwordMapper(SOME_PASSWORD_NAME);

    assertThat(password).hasValue(SOME_PASSWORD);
  }

  @Test
  public void passwordMappingWithoutValue() {
    passwordMap.put(SOME_PASSWORD_NAME, ANY_PASSWORD);

    Optional<byte[]> password = repository.passwordMapper(ANOTHER_PASSWORD_NAME);

    assertThat(password).isEmpty();
  }

  @Test
  public void passwordAdding() {
    repository.addPassword(SOME_PASSWORD_NAME, SOME_PASSWORD);

    assertThat(passwordMap)
        .hasSize(1)
        .containsEntry(SOME_PASSWORD_NAME, SOME_PASSWORD);
  }
}
