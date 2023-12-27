package com.github.codeteapot.testing;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressCreator {

  public static InetAddress getLocalHost() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  public static InetAddress getByName(String name) {
    try {
      return InetAddress.getByName(name);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
}
