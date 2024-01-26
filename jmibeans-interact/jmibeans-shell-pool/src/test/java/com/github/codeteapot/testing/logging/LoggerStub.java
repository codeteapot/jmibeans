package com.github.codeteapot.testing.logging;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.ALL;
import static java.util.logging.Logger.getLogger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerStub {

  private final Level previousLevel;
  private final PreviousHandlers previousHandlers;
  private final Logger logger;
  private final Handler handler;

  private LoggerStub(
      Level previousLevel,
      PreviousHandlers previousHandlers,
      Logger logger,
      Handler handler) {
    this.previousLevel = requireNonNull(logger.getLevel());
    this.previousHandlers = requireNonNull(previousHandlers);
    this.logger = requireNonNull(logger);
    this.handler = requireNonNull(handler);
  }

  public void restore() {
    logger.setLevel(previousLevel);
    logger.removeHandler(handler);
    previousHandlers.restore();
  }

  public static LoggerStub loggerStubFor(String name, Handler handler) {
    Logger logger = getLogger(name);
    Level previousLevel = logger.getLevel();
    logger.setLevel(ALL);
    PreviousHandlers previousHandlers = new PreviousHandlers(logger);
    logger.addHandler(handler);
    return new LoggerStub(previousLevel, previousHandlers, logger, handler);
  }
}
