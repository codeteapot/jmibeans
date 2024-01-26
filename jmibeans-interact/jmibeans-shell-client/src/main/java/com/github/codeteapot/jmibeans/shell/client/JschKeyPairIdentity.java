package com.github.codeteapot.jmibeans.shell.client;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.net.InetAddress.getLocalHost;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.jcraft.jsch.Identity;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

class JschKeyPairIdentity implements Identity {

  private static final Map<MachineShellPublicKeyType, Integer> KEY_PAIR_TYPE_MAP = Stream.of(
      new SimpleEntry<>(MachineShellPublicKeyType.RSA, KeyPair.RSA),
      new SimpleEntry<>(MachineShellPublicKeyType.DSA, KeyPair.DSA)).collect(
          toMap(Entry::getKey, Entry::getValue));

  private static final Map<Integer, String> ALG_NAME_MAP = Stream.of(
      new SimpleEntry<>(KeyPair.RSA, "ssh-rsa"),
      new SimpleEntry<>(KeyPair.DSA, "ssh-dsa")).collect(
          toMap(Entry::getKey, Entry::getValue));

  private final MachineShellIdentityName name;
  private final KeyPair keyPair;

  JschKeyPairIdentity(
      JschMachineShellClientImplementationContext context,
      MachineShellIdentityName name,
      MachineShellPublicKeyType publicKeyType,
      int publicKeySize) throws Exception {
    this.name = requireNonNull(name);
    keyPair = context.genKeyPair(KEY_PAIR_TYPE_MAP.get(publicKeyType), publicKeySize);
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAlgName() {
    return requireNonNull(ALG_NAME_MAP.get(keyPair.getKeyType()));
  }

  @Override
  public boolean isEncrypted() {
    return keyPair.isEncrypted();
  }

  @Override
  public byte[] getSignature(byte[] data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] getSignature(byte[] data, String alg) {
    return keyPair.getSignature(data, alg);
  }

  @Override
  public byte[] getPublicKeyBlob() {
    return keyPair.getPublicKeyBlob();
  }

  @Override
  public boolean setPassphrase(byte[] passphrase) throws JSchException {
    return false; // Irrelevant
  }

  @Override
  public boolean decrypt() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof JschKeyPairIdentity) {
      JschKeyPairIdentity identity = (JschKeyPairIdentity) obj;
      return name.equals(identity.name);
    }
    return false;
  }

  boolean match(MachineShellIdentityName name) {
    return name.equals(this.name);
  }

  void writePublicKey(OutputStream output) throws IOException {
    writePublicKey(output, () -> getLocalHost().getHostName());
  }

  void writePublicKey(OutputStream output, LocalHostNameSupplier localHostNameSupplier)
      throws IOException {
    try {
      keyPair.writePublicKey(output, format(
          "%s@%s",
          requireNonNull(getProperty("user.name")),
          localHostNameSupplier.get()));
    } catch (UnknownHostException e) {
      throw new IOException(e);
    }
  }
}
