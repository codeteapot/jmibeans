package com.github.codeteapot.jmibeans.machine;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface MachineNetwork {
  
  MachineNetworkName getName();
  
  InetAddress getAddress() throws UnknownHostException;
}
