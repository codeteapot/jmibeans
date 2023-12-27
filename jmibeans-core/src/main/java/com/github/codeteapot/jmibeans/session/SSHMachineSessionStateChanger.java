package com.github.codeteapot.jmibeans.session;

interface SSHMachineSessionStateChanger {

  void stateChange(SSHMachineSessionState newState);
}
