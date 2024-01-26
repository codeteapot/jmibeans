package com.github.codeteapot.jmibeans.shell.client;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

class JSchPath implements Path {

  private final FileSystem fileSystem;
  private final JSchPathNode first;

  JSchPath(FileSystem fileSystem, JSchPathNode first) {
    this.fileSystem = requireNonNull(fileSystem);
    this.first = requireNonNull(first);
  }

  JSchPath(FileSystem fileSystem, String first, String... more) {
    this(fileSystem, JSchPathNode.parse(first, more));
  }

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public boolean isAbsolute() {
    return first.isRoot();
  }

  @Override
  public Path getRoot() {
    return new JSchPath(fileSystem, first.isRoot() ? first.clone(1) : null);
  }

  @Override
  public Path getFileName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path getParent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getNameCount() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path getName(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean startsWith(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean startsWith(String other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean endsWith(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean endsWith(String other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path normalize() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path resolve(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path resolve(String other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path resolveSibling(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path resolveSibling(String other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path relativize(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public URI toUri() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path toAbsolutePath() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path toRealPath(LinkOption... options) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public File toFile() {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
      throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Path> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compareTo(Path other) {
    throw new UnsupportedOperationException();
  }

  String toRemote() {
    StringBuilder builder = new StringBuilder();
    first.remotePathBuild(builder, fileSystem.getSeparator());
    return builder.toString();
  }
}
