package com.github.codeteapot.jmibeans.port.docker;

import static java.lang.Character.digit;

import java.util.Arrays;

class DockerMonitorId {

  private static final char[] HEX_ARRAY = {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  };


  private final byte[] machineId;

  DockerMonitorId(String containerId) {
    machineId = fromHex(containerId);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(machineId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof DockerMonitorId) {
      DockerMonitorId monitorId = (DockerMonitorId) obj;
      return Arrays.equals(machineId, monitorId.machineId);
    }
    return false;
  }

  byte[] getMachineId() {
    return machineId;
  }

  @Override
  public String toString() {
    return bytesToHex(machineId);
  }

  private static byte[] fromHex(String str) {
    int len = str.length();
    byte[] bytes = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int i1 = digit(str.charAt(i), 16) << 4;
      int i2 = digit(str.charAt(i + 1), 16);
      bytes[i / 2] = (byte) (i1 + i2);
    }
    return bytes;
  }

  private static String bytesToHex(byte[] bytes) {
    char[] chars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int v = bytes[i] & 0xff;
      chars[i * 2] = HEX_ARRAY[v >>> 4];
      chars[i * 2 + 1] = HEX_ARRAY[v & 0x0f];
    }
    return new String(chars);
  }
}
