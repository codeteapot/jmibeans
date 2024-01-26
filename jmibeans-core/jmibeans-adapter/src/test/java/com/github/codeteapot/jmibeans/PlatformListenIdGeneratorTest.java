package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlatformListenIdGeneratorTest {

  private static final Function<Integer, Integer> RANDOM_INT = new Random()::nextInt;

  private static final PlatformListenId EXISTING_LISTEN_ID = new PlatformListenId(RANDOM_INT);
  private static final PlatformListenId NON_EXISTING_LISTEN_ID = new PlatformListenId(RANDOM_INT);

  private Set<PlatformListenId> alreadyUsed;

  @Mock
  private PlatformListenIdConstructor listenIdConstructor;

  private PlatformListenIdGenerator listenIdGenerator;

  @BeforeEach
  void setUp() {
    alreadyUsed = new HashSet<>();
    listenIdGenerator = new PlatformListenIdGenerator(RANDOM_INT, alreadyUsed, listenIdConstructor);
  }

  @Test
  void generateNonExistingListenId() {
    alreadyUsed.add(EXISTING_LISTEN_ID);
    when(listenIdConstructor.construct(RANDOM_INT))
        .thenReturn(EXISTING_LISTEN_ID, NON_EXISTING_LISTEN_ID);

    PlatformListenId listenId = listenIdGenerator.generate();

    assertThat(listenId).isEqualTo(NON_EXISTING_LISTEN_ID);
  }
}
