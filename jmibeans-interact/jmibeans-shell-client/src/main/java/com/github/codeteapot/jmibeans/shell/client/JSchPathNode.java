package com.github.codeteapot.jmibeans.shell.client;

import static java.lang.Integer.MAX_VALUE;
import static java.util.stream.Stream.concat;

import java.util.stream.Stream;

abstract class JSchPathNode {

  static final String SEPARATOR = "/";

  private final JSchPathNode next;

  protected JSchPathNode(JSchPathNode next) {
    this.next = next;
  }

  JSchPathNode clone(int count) {
    return count > 0 && next != null ? newInstance(next.clone(count - 1)) : null;
  }

  abstract boolean isRoot();

  void remotePathBuild(StringBuilder builder, String separator) {
    appendName(builder);
    if (next != null) {
      builder.append(SEPARATOR);
      next.remotePathBuild(builder, separator);
    }
  }

  @Override
  protected JSchPathNode clone() {
    return clone(MAX_VALUE);
  }

  protected abstract JSchPathNode newInstance(JSchPathNode next);

  protected abstract void appendName(StringBuilder remotePath);

  static JSchPathNode parse(String first, String... more) {
    JSchPathNode node = parseNode(concat(Stream.of(first.split(SEPARATOR)), Stream.of(more))
        .map(String.class::cast)
        .map(JSchPathNode::encodePart)
        .filter(part -> !part.isEmpty())
        .toArray(String[]::new), 0);
    return first.startsWith(SEPARATOR) ? new JSchPathRootNode(node) : node;

  }

  private static JSchPathNode parseNode(String[] parts, int offset) {
    return offset < parts.length
        ? new JSchPathNamedNode(parseNode(parts, offset + 1), parts[offset])
        : null;
  }

  private static String encodePart(String part) {
    return part;
  }
}
