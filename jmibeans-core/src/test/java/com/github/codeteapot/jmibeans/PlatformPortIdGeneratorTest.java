package com.github.codeteapot.jmibeans;

import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.PlatformPortId;
import com.github.codeteapot.jmibeans.PlatformPortIdConstructor;
import com.github.codeteapot.jmibeans.PlatformPortIdGenerator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlatformPortIdGeneratorTest {

  private static final Random USED_RANDOM = new Random();
  
  private static final PlatformPortId EXISTING_PORT_ID = new PlatformPortId(USED_RANDOM);
  private static final PlatformPortId NON_EXISTING_PORT_ID = new PlatformPortId(USED_RANDOM);
  
  private Set<PlatformPortId> alreadyUsed;

  @Mock
  private PlatformPortIdConstructor portIdConstructor;

  private PlatformPortIdGenerator portIdGenerator;

  @BeforeEach
  public void setUp() {
    alreadyUsed = new HashSet<>();
    portIdGenerator = new PlatformPortIdGenerator(USED_RANDOM, alreadyUsed, portIdConstructor);
  }

  @Test
  public void generateNonExistingPortId() {
    alreadyUsed.add(EXISTING_PORT_ID);
    when(portIdConstructor.construct(USED_RANDOM))
        .thenReturn(EXISTING_PORT_ID, NON_EXISTING_PORT_ID);

    PlatformPortId portId = portIdGenerator.generate();
    
    Assertions.assertThat(portId).isEqualTo(NON_EXISTING_PORT_ID);
  }
}
