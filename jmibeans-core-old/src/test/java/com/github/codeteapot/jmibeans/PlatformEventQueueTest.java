package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.PlatformEventQueue;
import com.github.codeteapot.jmibeans.event.PlatformListener;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlatformEventQueueTest {

  private List<PlatformListener> listeners;

  private PlatformEventQueue eventQueue;

  @BeforeEach
  public void setUp() {
    listeners = new ArrayList<>();
    eventQueue = new PlatformEventQueue(listeners);
  }

  @Test
  public void addListener(@Mock PlatformListener someListener) {
    eventQueue.addListener(someListener);

    assertThat(listeners).containsExactly(someListener);
  }

  @Test
  public void removeListener(@Mock PlatformListener someListener) {
    listeners.add(someListener);

    eventQueue.removeListener(someListener);

    assertThat(listeners).isEmpty();
  }
}
