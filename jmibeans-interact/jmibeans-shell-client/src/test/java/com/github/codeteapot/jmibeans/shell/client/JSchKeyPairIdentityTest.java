package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType.DSA;
import static com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType.RSA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.security.auth.callback.CallbackHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JSchKeyPairIdentityTest {

  private static final MachineShellIdentityName ANY_NAME = new MachineShellIdentityName(
      "any-identity");
  private static final MachineShellPublicKeyType ANY_PUBLIC_KEY_TYPE = RSA;
  private static final int ANY_PUBLIC_KEY_SIZE = 1024;

  private static final byte[] ANY_DATA = {};
  private static final String ANY_ALG = "ssh-rsa";

  private static final byte[] NULL_PASSPHRASE = null;

  private static final MachineShellPublicKeyType RSA_PUBLIC_KEY_TYPE = RSA;
  private static final MachineShellPublicKeyType DSA_PUBLIC_KEY_TYPE = DSA;

  private static final MachineShellIdentityName SOME_NAME = new MachineShellIdentityName(
      "some-identity");
  private static final MachineShellIdentityName ANOTHER_NAME = new MachineShellIdentityName(
      "another-identity");

  private static final int SOME_HASH_CODE = 1227641207;

  private static final UnknownHostException SOME_UNKOWN_HOST_EXCEPTION = new UnknownHostException();

  private JSchMachineShellClientEngine engine;

  @BeforeEach
  void setUp(@Mock CallbackHandler anyCallbackHandler) {
    engine = new JSchMachineShellClientEngine(anyCallbackHandler);
  }

  @Test
  void getNameIsNotNeeded() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    Throwable e = catchThrowable(() -> identity.getName());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getArgNameForRSA() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        RSA_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    String algName = identity.getAlgName();

    assertThat(algName).isEqualTo("ssh-rsa");
  }

  @Test
  void getArgNameForDSA() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        DSA_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    String algName = identity.getAlgName();

    assertThat(algName).isEqualTo("ssh-dsa");
  }

  @Test
  void isPretendedToBeNotEncrypted() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    boolean encrypted = identity.isEncrypted();

    assertThat(encrypted).isFalse();
  }

  @Test
  void getSignatureWithoutAlgIsNotNeeded() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    Throwable e = catchThrowable(() -> identity.getSignature(ANY_DATA));

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getSignatureWithAlg() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    byte[] signature = identity.getSignature(ANY_DATA, ANY_ALG);

    assertThat(signature).isNotEmpty();
  }

  @Test
  void getPublicKeyBlob() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    byte[] publicKeyBlob = identity.getPublicKeyBlob();

    assertThat(publicKeyBlob).isNotEmpty();
  }

  @Test
  void setPasswordIsIrrelevant() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    boolean applied = identity.setPassphrase(NULL_PASSPHRASE);

    assertThat(applied).isFalse();
  }

  @Test
  void decryptIsNotNeeded() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    Throwable e = catchThrowable(() -> identity.decrypt());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void clearIsNotNeeded() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    Throwable e = catchThrowable(() -> identity.clear());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void hashCodeBasedOnName() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        SOME_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    int result = identity.hashCode();

    assertThat(result).isEqualTo(SOME_HASH_CODE);
  }

  @Test
  void equalByJavaRef() throws Exception {
    JSchKeyPairIdentity someIdentity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);
    JSchKeyPairIdentity anotherIdentity = someIdentity;

    boolean result = someIdentity.equals(anotherIdentity);

    assertThat(result).isTrue();
  }

  @Test
  void equalByName() throws Exception {
    JSchKeyPairIdentity someIdentity = new JSchKeyPairIdentity(
        engine,
        SOME_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);
    JSchKeyPairIdentity anotherIdentity = new JSchKeyPairIdentity(
        engine,
        SOME_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    boolean result = someIdentity.equals(anotherIdentity);

    assertThat(result).isTrue();
  }

  @Test
  void notEqualByName() throws Exception {
    JSchKeyPairIdentity someIdentity = new JSchKeyPairIdentity(
        engine,
        SOME_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);
    JSchKeyPairIdentity anotherIdentity = new JSchKeyPairIdentity(
        engine,
        ANOTHER_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    boolean result = someIdentity.equals(anotherIdentity);

    assertThat(result).isFalse();
  }

  @Test
  void notEqualByJavaType() throws Exception {
    JSchKeyPairIdentity someIdentity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);
    Object anotherIdentity = new Object();

    boolean result = someIdentity.equals(anotherIdentity);

    assertThat(result).isFalse();
  }

  @Test
  void matchByName() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        SOME_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    boolean result = identity.match(SOME_NAME);

    assertThat(result).isTrue();
  }

  @Test
  void notNatchByName() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        SOME_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    boolean result = identity.match(ANOTHER_NAME);

    assertThat(result).isFalse();
  }

  @Test
  void writePublicKey() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    try (ByteArrayOutputStream someOutput = new ByteArrayOutputStream()) {
      identity.writePublicKey(someOutput);

      assertThat(someOutput.toByteArray()).isNotEmpty();
    }
  }

  @Test
  void failWhenWritingPublicKeyOnUnknownLocalHost() throws Exception {
    JSchKeyPairIdentity identity = new JSchKeyPairIdentity(
        engine,
        ANY_NAME,
        ANY_PUBLIC_KEY_TYPE,
        ANY_PUBLIC_KEY_SIZE);

    try (ByteArrayOutputStream someOutput = new ByteArrayOutputStream()) {
      Throwable e = catchThrowable(() -> identity.writePublicKey(someOutput, () -> {
        throw SOME_UNKOWN_HOST_EXCEPTION;
      }));

      assertThat(e)
          .isInstanceOf(IOException.class)
          .hasCause(SOME_UNKOWN_HOST_EXCEPTION);
      assertThat(someOutput.toByteArray()).isEmpty();
    }
  }
}
