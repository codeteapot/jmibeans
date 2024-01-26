package com.github.codeteapot.jmibeans.profile;

public class MachineBuildingException extends Exception {

  private static final long serialVersionUID = 1L;

  public MachineBuildingException(String message) {
    super(message);
  }

  public MachineBuildingException(Throwable cause) {
    super(cause);
  }

  public MachineBuildingException(String message, Throwable cause) {
    super(message, cause);
  }
}
