package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

class JSchPathNamedNode extends JSchPathNode {

  private final String name;

  JSchPathNamedNode(JSchPathNode next, String name) {
    super(next);
    this.name = requireNonNull(name);
  }

  @Override
  boolean isRoot() {
    return false;
  }

  @Override
  protected JSchPathNode newInstance(JSchPathNode next) {
    return new JSchPathNamedNode(next, name);
  }

  @Override
  protected void appendName(StringBuilder remotePath) {
    remotePath.append(name);
  }
}
