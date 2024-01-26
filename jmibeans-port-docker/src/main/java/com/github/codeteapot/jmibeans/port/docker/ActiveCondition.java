package com.github.codeteapot.jmibeans.port.docker;

interface ActiveCondition {

  boolean test() throws InterruptedException;
}
