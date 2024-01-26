package com.github.codeteapot.jmibeans.shell.client;

import java.net.UnknownHostException;

@FunctionalInterface
interface LocalHostNameSupplier {

  String get() throws UnknownHostException;
}
