package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue that allows platform events to be dispatched locally.
 */
public class PlatformEventQueue implements PlatformEventTarget {

  private final BlockingQueue<PlatformEventQueueDispatch<?, ?>> dispatchQueue;
  private final List<PlatformListener> listeners;

  /**
   * Queue without events to be dispatched and without added listeners.
   */
  public PlatformEventQueue() {
    this(new LinkedList<>());
  }

  PlatformEventQueue(List<PlatformListener> listeners) {
    dispatchQueue = new LinkedBlockingQueue<>();
    this.listeners = requireNonNull(listeners);
  }

  @Override
  public void fireAvailable(MachineAvailableEvent event) {
    dispatchQueue.offer(new PlatformEventQueueDispatch<>(
        event,
        listeners,
        PlatformListener::machineAvailable));
  }

  @Override
  public void fireLost(MachineLostEvent event) {
    dispatchQueue.offer(new PlatformEventQueueDispatch<>(
        event,
        listeners,
        PlatformListener::machineLost));
  }

  /**
   * Add a listener that will be taken into account when dispatching events.
   *
   * <p>The same listener can be added more than once. The same event will be listened to by a
   * listener as many times as it has been added.
   *
   * <p>Events that have not yet been dispatched will be listened to by the added listener.
   *
   * @param listener The listener to add.
   */
  public void addListener(PlatformListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener from existing ones. It has no effect if the listener was not added
   * previously.
   *
   * <p>This takes into account that the same listener can be added more than once. That is, only
   * one is removed, the rest will remain listening.
   *
   * <p>Events that have not yet been dispatched will no longer be listened to by the removed
   * listener.
   *
   * @param listener The listener to remove.
   */
  public void removeListener(PlatformListener listener) {
    listeners.remove(listener);
  }

  /**
   * Dispatches events in the order in which they are fired.
   *
   * <p>Each event involves a call to the corresponding operation of all added listeners at the time
   * it is processed.
   *
   * <p>This is a blocking operation. It ends when the thread in which it is executed is
   * interrupted.
   *
   * <p>This operation can safely be called more than once. The same event will only be processed in
   * one of the calls. This allows parallelization of the process for each event.
   *
   * @throws InterruptedException When the thread in which the operation is executed is interrupted.
   */
  public void dispatchEvents() throws InterruptedException {
    while (true) {
      dispatchQueue.take().perform();
    }
  }
}
