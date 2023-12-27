package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.event.PlatformListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of {@link PlatformEventSource} based on an in-memory blocking queue.
 *
 * <p>It serves as a reference to show the relationship between the event source and the
 * {@link PlatformListener}s.
 */
public class PlatformEventQueue implements PlatformEventSource {

  private final BlockingQueue<EventDispatcher<?, ?>> dispatcherQueue;
  private final List<PlatformListener> listeners;

  /**
   * Create a queue with default properties.
   */
  public PlatformEventQueue() {
    this(new LinkedList<>());
  }

  PlatformEventQueue(List<PlatformListener> listeners) {
    dispatcherQueue = new LinkedBlockingQueue<>();
    this.listeners = requireNonNull(listeners);
  }

  @Override
  public void fireEvent(MachineAvailableEvent event) {
    dispatcherQueue.offer(new EventDispatcher<>(
        event,
        listeners,
        PlatformListener::machineAvailable));
  }

  @Override
  public void fireEvent(MachineLostEvent event) {
    dispatcherQueue.offer(new EventDispatcher<>(
        event,
        listeners,
        PlatformListener::machineLost));
  }

  /**
   * Add a new listener to the queue.
   *
   * <p>A listener instance receives the same event as many times as it has been added to the queue.
   * The order in which the added listeners receive the produced events is independent of the order
   * in which they were added.
   * 
   * <p>The listener will only receive the events that have occurred since the moment it was added.
   *
   * @param listener The listener to be added.
   */
  public void addListener(PlatformListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes the specified listener from the queue.
   *
   * <p>Reverts the result of only one of the additions made to the queue.
   *
   * <p>The listener will stop listening to the events that occur in the queue from the moment they
   * have been removed as many times as they have been added.
   *
   * @param listener The listener to be added.
   */
  public void removeListener(PlatformListener listener) {
    listeners.remove(listener);
  }

  /**
   * Processes indefinitely and sequentially the events that are fired by the queue.
   *
   * <p>Each event processed implies a call to the corresponding method of each added listener.
   * 
   * <p>This is a blocking operation. It does not support more than one concurrent execution on the
   * same instance. Otherwise, the results are unexpected.
   *
   * @throws InterruptedException If the dispatch thread is interrupted.
   */
  public void dispatchEvents() throws InterruptedException {
    while (true) {
      dispatcherQueue.take().dispatch();
    }
  }
}
