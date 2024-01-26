package com.github.codeteapot.jmibeans.shell.client;

class JSchPathRootNode extends JSchPathNode {

  JSchPathRootNode(JSchPathNode next) {
    super(next);
  }

  @Override
  boolean isRoot() {
    return true;
  }

  @Override
  protected JSchPathNode newInstance(JSchPathNode next) {
    return new JSchPathRootNode(next);
  }

  @Override
  protected void appendName(StringBuilder remotePath) {}
}
