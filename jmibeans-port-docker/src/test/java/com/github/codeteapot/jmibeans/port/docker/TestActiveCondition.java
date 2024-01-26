package com.github.codeteapot.jmibeans.port.docker;

import java.util.concurrent.Semaphore;

class TestActiveCondition implements ActiveCondition {

  private final Semaphore semaphore;
  private boolean value;

  TestActiveCondition() {
    semaphore = new Semaphore(0);
    value = false;
  }

  void pollEvents(int amount) {
    value = true;
    semaphore.release(amount);
  }

  void finish() {
    value = false;
    semaphore.release();
  }

  @Override
  public boolean test() throws InterruptedException {
    semaphore.acquire();
    return value;
  }
}
