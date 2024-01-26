package com.github.codeteapot.jmibeans;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

class PlatformEventQueueDispatch<E extends EventObject, L extends EventListener> {

  private static final Logger logger = getLogger(PlatformEventQueue.class.getName());

  private final E event;
  private final Collection<L> listeners;
  private final BiConsumer<L, E> action;

  PlatformEventQueueDispatch(E event, Collection<L> listeners, BiConsumer<L, E> action) {
    this.event = requireNonNull(event);
    this.listeners = unmodifiableCollection(listeners);
    this.action = requireNonNull(action);
  }

  void perform() {
    listeners.forEach(this::perform);
  }

  private void perform(L listener) {
    try {
      action.accept(listener, event);
    } catch (RuntimeException e) {
      logger.log(SEVERE, "Error occurred while dispatching event", e);
    }
  }
}
