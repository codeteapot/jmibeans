package com.github.codeteapot.jmibeans;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineFacet;
import com.github.codeteapot.jmibeans.machine.MachineRef;
import com.github.codeteapot.jmibeans.machine.UnknownUserException;
import com.github.codeteapot.jmibeans.port.MachineSessionHostResolutionException;
import com.github.codeteapot.jmibeans.session.MachineCommandExecutionException;
import com.github.codeteapot.jmibeans.session.MachineSession;
import com.github.codeteapot.jmibeans.session.MachineSessionException;
import java.io.IOException;

class TestSayMessageMachineFacet implements MachineFacet {

  private final MachineContext context;

  TestSayMessageMachineFacet(MachineContext context) {
    this.context = requireNonNull(context);
  }

  @Override
  public MachineRef getRef() {
    throw new UnsupportedOperationException();
  }

  void sayMessage(String username, String message) {
    try (MachineSession session = context.getSession(username)) {
      session.execute(new TestSayMessageCommand(message));
    } catch (IOException
        | UnknownUserException
        | MachineSessionHostResolutionException
        | MachineSessionException
        | MachineCommandExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
