package com.github.codeteapot.jmibeans.depot.dns.catalog;

import com.github.codeteapot.jmibeans.machine.MachineNetwork;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.bouncycastle.util.Arrays;

// TODO Enable reverse domain creation
class DNSServiceAddress {

  private final InetAddress address;
  private final short prefixLength;
  private final byte[] netBytes;
  private final byte[] hostBytes;

  DNSServiceAddress(MachineNetwork network) throws UnknownHostException {
    address = network.getAddress();
    prefixLength = network.getPrefixLength();
    byte[] addressBytes = address.getAddress();
    netBytes = new byte[addressBytes.length];
    hostBytes = new byte[addressBytes.length];
    int[] netMask = new int[addressBytes.length];
    Arrays.clear(netMask);
    for (int i = 0, m = 128; i < prefixLength; ++i, m = m == 1 ? 128 : m >>> 1) {
      netMask[i / 8] |= m;
    }
    for (int i = 0; i < addressBytes.length; ++i) {
      netBytes[i] = (byte) (addressBytes[i] & netMask[i]);
      hostBytes[i] = (byte) (addressBytes[i] & ~netMask[i]);
    }
  }

  String getInversePrefix() {
    StringBuilder inversePrefix = new StringBuilder();
    String sep = "";
    for (int i = prefixLength / 8 + (prefixLength % 8 == 0 ? -1 : 0); i >= 0; --i) {
      int b = netBytes[i] & 0xff;
      inversePrefix.append(sep).append(b);
      sep = ".";
    }
    return inversePrefix.toString();
  }

  String getAddress() {
    return address.getHostAddress();
  }

  String getAddressHostPart() {
    StringBuilder hostPart = new StringBuilder();
    String sep = "";
    for (int i = prefixLength / 8; i < hostBytes.length; ++i) {
      int b = hostBytes[i];
      hostPart.append(sep).append(b);
      sep = ".";
    }
    return hostPart.toString();
  }
}
