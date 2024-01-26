package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PlatformEventQueue implements PlatformEventTarget {

  private final BlockingQueue<PlatformEventQueueDispatch<?, ?>> dispatchQueue;
  private final List<PlatformListener> listeners;

  public PlatformEventQueue() {
    this(new LinkedList<>());
  }

  PlatformEventQueue(List<PlatformListener> listeners) {
    dispatchQueue = new LinkedBlockingQueue<>();
    this.listeners = requireNonNull(listeners);
  }

  @Override
  public void fireAvailableEvent(MachineAvailableEvent event) {
    dispatchQueue.offer(new PlatformEventQueueDispatch<>(
        event,
        listeners,
        PlatformListener::machineAvailable));
  }

  @Override
  public void fireLostEvent(MachineLostEvent event) {
    dispatchQueue.offer(new PlatformEventQueueDispatch<>(
        event,
        listeners,
        PlatformListener::machineLost));
  }

  public void addListener(PlatformListener listener) {
    listeners.add(listener);
  }

  public void removeListener(PlatformListener listener) {
    listeners.remove(listener);
  }

  public void dispatchEvents() throws InterruptedException {
    while (true) {
      dispatchQueue.take().perform();
    }
  }
}
