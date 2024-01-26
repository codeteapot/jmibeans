package com.github.codeteapot.jmibeans.shell.client;

import static com.github.codeteapot.jmibeans.shell.client.JSchPathNode.SEPARATOR;
import static java.util.Objects.requireNonNull;

import com.jcraft.jsch.ChannelSftp;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

class JSchFileSystem extends FileSystem {

  private final FileSystemProvider provider;
  private final ChannelSftp jschChannel;
  private final Runnable discardAction;

  JSchFileSystem(FileSystemProvider provider, ChannelSftp jschChannel, Runnable discardAction) {
    this.provider = requireNonNull(provider);
    this.jschChannel = requireNonNull(jschChannel);
    this.discardAction = requireNonNull(discardAction);
  }

  @Override
  public FileSystemProvider provider() {
    return provider;
  }

  @Override
  public boolean isOpen() {
    return true; // TODO State pattern
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public String getSeparator() {
    return SEPARATOR;
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path getPath(String first, String... more) {
    return new JSchPath(this, first, more);
  }

  @Override
  public PathMatcher getPathMatcher(String syntaxAndPattern) {
    throw new UnsupportedOperationException();
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchService newWatchService() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
    jschChannel.disconnect();
    discardAction.run(); // TODO State pattern
  }

  void closeNow() {
    jschChannel.disconnect();
  }
}
