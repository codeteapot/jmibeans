package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.session.MachineCommand;
import com.github.codeteapot.jmibeans.session.MachineCommandExecution;
import java.nio.charset.Charset;
import java.util.Objects;

class TestSayMessageCommand implements MachineCommand<Void> {

  private final String message;

  TestSayMessageCommand(String message) {
    this.message = Objects.requireNonNull(message);
  }

  @Override
  public String getSentence() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MachineCommandExecution<Void> getExecution(Charset charset) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return message.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof TestSayMessageCommand) {
      TestSayMessageCommand command = (TestSayMessageCommand) obj;
      return message.equals(command.message);
    }
    return false;
  }
}
