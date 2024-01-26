package com.github.codeteapot.jmibeans.testing.shell;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

class TestThreadFactory implements ThreadFactory {

  private final Set<Thread> threads;

  TestThreadFactory() {
    threads = new HashSet<>();
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    threads.add(t);
    return t;
  }

  void interruptAll() {
    threads.forEach(Thread::interrupt);
  }

}
