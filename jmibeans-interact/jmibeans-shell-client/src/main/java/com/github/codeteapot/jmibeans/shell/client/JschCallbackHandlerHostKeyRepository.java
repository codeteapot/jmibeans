package com.github.codeteapot.jmibeans.shell.client;

import static java.lang.String.format;
import static java.net.InetAddress.getByName;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback //
    .MachineShellHostCallback;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.callback.MachineShellHostStatus;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;

class JschCallbackHandlerHostKeyRepository implements HostKeyRepository {

  private static final Map<MachineShellHostStatus, Integer> CHECK_RESULT_MAP = Stream.of(
      new SimpleEntry<>(MachineShellHostStatus.KNOWN, HostKeyRepository.OK),
      new SimpleEntry<>(MachineShellHostStatus.UNKNOWN, HostKeyRepository.NOT_INCLUDED),
      new SimpleEntry<>(MachineShellHostStatus.CHANGED, HostKeyRepository.CHANGED)).collect(
          toMap(Entry::getKey, Entry::getValue));

  private static final Pattern ADDRESS_PATTERN = compile("^\\[(.+)\\]:\\d+$");

  private final CallbackHandler callbackHandler;

  JschCallbackHandlerHostKeyRepository(CallbackHandler callbackHandler) {
    this.callbackHandler = requireNonNull(callbackHandler);
  }

  @Override
  public String getKnownHostsRepositoryID() {
    throw new UnsupportedOperationException();
  }

  @Override
  public HostKey[] getHostKey() {
    throw new UnsupportedOperationException();
  }

  @Override
  public HostKey[] getHostKey(String host, String type) {
    return new HostKey[0]; // Irrelevant
  }

  @Override
  public int check(String host, byte[] key) {
    try {
      MachineShellHostCallback hostCallback = new MachineShellHostCallback(addressOf(host), key);
      callbackHandler.handle(new Callback[] {
          hostCallback
      });
      return CHECK_RESULT_MAP.get(hostCallback.getStatus());
    } catch (UnsupportedCallbackException | IOException e) {
      // TODO Log error
      return NOT_INCLUDED;
    }
  }

  @Override
  public void add(HostKey hostkey, UserInfo ui) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(String host, String type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(String host, String type, byte[] key) {
    throw new UnsupportedOperationException();
  }

  private InetAddress addressOf(String host) throws UnknownHostException {
    Matcher matcher = ADDRESS_PATTERN.matcher(host);
    if (matcher.find()) {
      return getByName(matcher.group(1));
    }
    throw new UnknownHostException(format("From %s", host));
  }
}
