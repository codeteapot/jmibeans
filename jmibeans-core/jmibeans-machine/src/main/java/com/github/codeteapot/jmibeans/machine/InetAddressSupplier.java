package com.github.codeteapot.jmibeans.machine;

import java.net.InetAddress;
import java.net.UnknownHostException;

@FunctionalInterface
interface InetAddressSupplier {

  InetAddress getAddress() throws UnknownHostException;
}
