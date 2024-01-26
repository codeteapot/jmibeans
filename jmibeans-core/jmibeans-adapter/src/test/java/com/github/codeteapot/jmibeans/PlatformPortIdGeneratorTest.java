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
class PlatformPortIdGeneratorTest {

  private static final Function<Integer, Integer> RANDOM_INT = new Random()::nextInt;

  private static final PlatformPortId EXISTING_PORT_ID = new PlatformPortId(RANDOM_INT);
  private static final PlatformPortId NON_EXISTING_PORT_ID = new PlatformPortId(RANDOM_INT);

  private Set<PlatformPortId> alreadyUsed;

  @Mock
  private PlatformPortIdConstructor portIdConstructor;

  private PlatformPortIdGenerator portIdGenerator;

  @BeforeEach
  void setUp() {
    alreadyUsed = new HashSet<>();
    portIdGenerator = new PlatformPortIdGenerator(RANDOM_INT, alreadyUsed, portIdConstructor);
  }

  @Test
  void generateNonExistingPortId() {
    alreadyUsed.add(EXISTING_PORT_ID);
    when(portIdConstructor.construct(RANDOM_INT))
        .thenReturn(EXISTING_PORT_ID, NON_EXISTING_PORT_ID);

    PlatformPortId portId = portIdGenerator.generate();

    assertThat(portId).isEqualTo(NON_EXISTING_PORT_ID);
  }
}
