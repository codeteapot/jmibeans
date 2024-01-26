package com.github.codeteapot.testing.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressCreator {

  private InetAddressCreator() {}

  public static InetAddress getByName(String host) {
    try {
      return InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
