package com.github.codeteapot.jmibeans.shell.client;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

// https://stackoverflow.com/questions/22966176/creating-a-custom-filesystem-implementation-in-java
/*
 * Proposal for thread a safe implementation
 *
 * ChannelSftp implementation with thread local underlying instance
 */
class JSchFileSystemProvider extends FileSystemProvider {

  private static final Set<OpenOption> DOWNLOAD_OPEN_OPTIONS = emptySet();
  private static final Set<OpenOption> UPLOAD_OPEN_OPTIONS = Stream.of(
      WRITE,
      CREATE,
      TRUNCATE_EXISTING).collect(toSet());

  private final ChannelSftp jschChannel;
  private final Consumer<JSchFileSystemProvider> removeAction;
  private JSchFileSystem fileSystem;

  JSchFileSystemProvider(ChannelSftp jschChannel, Consumer<JSchFileSystemProvider> removeAction) {
    this.jschChannel = requireNonNull(jschChannel);
    this.removeAction = requireNonNull(removeAction);
    fileSystem = null;
  }

  @Override
  public String getScheme() {
    throw new UnsupportedOperationException();
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    throw new UnsupportedOperationException();
  }

  public FileSystem getFileSystem() {
    if (fileSystem == null) {
      fileSystem = new JSchFileSystem(this, jschChannel, this::discardFileSystem);
    }
    return fileSystem;
  }

  @Override
  public Path getPath(URI uri) {
    throw new UnsupportedOperationException();
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public SeekableByteChannel newByteChannel(
      Path path,
      Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) throws IOException {
    if (options.equals(DOWNLOAD_OPEN_OPTIONS)) {
      return new JSchDownloadSeekableByteChannel(jschChannel, toJSchPath(path).toRemote());
    }
    if (options.equals(UPLOAD_OPEN_OPTIONS)) {
      return new JSchUploadSeekableByteChannel(jschChannel, toJSchPath(path).toRemote());
    }
    throw new IOException("Unsupported open options: " + options);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
      throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Path path) throws IOException {
    String remotePath = toJSchPath(path).toRemote();
    try {
      if (jschChannel.stat(remotePath).isDir()) {
        jschChannel.rmdir(remotePath);
      } else {
        jschChannel.rm(remotePath);
      }
    } catch (SftpException e) {
      throw toIOException(e, remotePath);
    }
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSameFile(Path path, Path path2) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isHidden(Path path) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public FileStore getFileStore(Path path) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) throws IOException {
    if (modes.length > 0) {
      throw new UnsupportedOperationException();
    }
    String remotePath = toJSchPath(path).toRemote();
    try {
      jschChannel.stat(remotePath);
    } catch (SftpException e) {
      throw toIOException(e, remotePath);
    }
  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(
      Path path,
      Class<V> type,
      LinkOption... options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(
      Path path,
      Class<A> type,
      LinkOption... options) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
      throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options)
      throws IOException {
    throw new UnsupportedOperationException();
  }

  void dispose() { // TODO State pattern
    if (fileSystem != null) {
      fileSystem.closeNow();
      fileSystem = null;
    }
  }

  private JSchPath toJSchPath(Path path) {
    try {
      if (fileSystem == null) {
        throw new IllegalStateException("File system is null");
      }
      if (fileSystem != path.getFileSystem()) {
        throw new IllegalArgumentException("Not the same file system");
      }
      return (JSchPath) path;
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("Unsupported path type", e);
    }
  }

  private void discardFileSystem() {
    removeAction.accept(this);
    fileSystem = null;
  }

  private static IOException toIOException(SftpException e, String remotePath) {
    if (e.id == SSH_FX_NO_SUCH_FILE) {
      return new NoSuchFileException(remotePath);
    }
    return new IOException(e);
  }
}
