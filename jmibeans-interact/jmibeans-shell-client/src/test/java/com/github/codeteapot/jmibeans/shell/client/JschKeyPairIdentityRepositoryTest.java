package com.github.codeteapot.jmibeans.shell.client;

import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JschKeyPairIdentityRepositoryTest {

  private static final byte[] ANY_IDENTITY_BYTES = {};
  private static final byte[] ANY_IDENTITY_BLOB = {};

  private final Set<JschKeyPairIdentity> identities = new HashSet<>();

  private JschKeyPairIdentityRepository repository;

  @BeforeEach
  void setUp() {
    repository = new JschKeyPairIdentityRepository(identities);
  }

  @AfterEach
  void tearDown() {
    identities.clear();
  }

  @Test
  void getNameIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.getName());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getStatusIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.getStatus());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getIdentitiesAsVector(@Mock JschKeyPairIdentity someIdentity) {
    identities.add(someIdentity);

    Vector<?> result = repository.getIdentities();

    assertThat(result).isEqualTo(Stream.of(someIdentity).collect(toCollection(Vector::new)));
  }

  @Test
  void addIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.add(ANY_IDENTITY_BYTES));

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void removeIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.remove(ANY_IDENTITY_BLOB));

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void removeAllIsNotNeeded() {
    Throwable e = catchThrowable(() -> repository.removeAll());

    assertThat(e).isInstanceOf(UnsupportedOperationException.class);
  }
}
