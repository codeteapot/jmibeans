package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import java.util.Vector;
import com.jcraft.jsch.Identity;
import com.jcraft.jsch.IdentityRepository;

class JschKeyPairIdentityRepository implements IdentityRepository {

  private final Set<Identity> identities;

  JschKeyPairIdentityRepository(Set<? extends Identity> identities) {
    this.identities = unmodifiableSet(identities);
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getStatus() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector<?> getIdentities() {
    return new Vector<>(identities);
  }

  @Override
  public boolean add(byte[] identity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(byte[] blob) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeAll() {
    throw new UnsupportedOperationException();
  }
}
