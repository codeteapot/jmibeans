package com.github.codeteapot.testing.logging;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.stream.Stream;

class PreviousHandlers {

  private final PreviousHandlers parent;
  private final Logger logger;
  private final Handler[] handlers;

  PreviousHandlers(Logger logger) {
    parent = ofNullable(logger.getParent())
        .map(PreviousHandlers::new)
        .orElse(null);
    this.logger = requireNonNull(logger);
    handlers = logger.getHandlers();
    Stream.of(handlers).forEach(logger::removeHandler);
  }

  void restore() {
    ofNullable(parent).ifPresent(PreviousHandlers::restore);
    Stream.of(handlers).forEach(logger::addHandler);
  }
}
