package com.github.codeteapot.jmibeans.examples.httpcert;

import static java.lang.String.format;

import com.github.codeteapot.jmibeans.machine.MachineAgent;
import com.github.codeteapot.jmibeans.machine.MachineNetworkAddressBinding;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.profile.MachineBuilderContext;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClient;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientContext;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientException;
import com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKey;
import com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKeyType;
import com.github.codeteapot.jmibeans.shell.client.pool.PoolingMachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.host.DefinedMachineShellHostStore;
import com.github.codeteapot.jmibeans.shell.client.security.auth.host.MachineShellHost;
import com.github.codeteapot.jmibeans.shell.client.security.auth.host.MachineShellKnownHosts;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellAuthorizedUsers;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellUserRepository;
import com.github.codeteapot.jmibeans.shell.mutable.MachineShellConnectionFactoryLifecycle;
import com.github.codeteapot.jmibeans.shell.mutable.MutableAddressMachineShellConectionFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SimpleShellProvider {

  private static final String SUPPORTED_URL_FORMAT = "ftp://%s:%s@%s%s";

  private final MachineShellClientContext clientContext;

  public SimpleShellProvider(
      String publicKeyRepositoryHost,
      String publicKeyRepositoryUser,
      File publicKeyRepositoryPasswordFile,
      String publicKeyRepositoryPath,
      MachineShellPublicKeyType publicKeyType,
      int publicKeySize) throws IOException, MachineShellClientException {
    Map<InetAddress, MachineShellHost> hostMap = new HashMap<>();
    clientContext = new MachineShellClientContext(
        new MachineShellKnownHosts(
            anyHost -> true,
            new DefinedMachineShellHostStore(hostMap::get, hostMap::put)));
    try (OutputStream pubKeyOutput = new URL(format(
        SUPPORTED_URL_FORMAT,
        publicKeyRepositoryUser,
        readFrom(publicKeyRepositoryPasswordFile),
        publicKeyRepositoryHost,
        publicKeyRepositoryPath)).openConnection().getOutputStream()) {
      clientContext.generateIdentity(
          new MachineShellIdentityName("any"),
          new MachineShellPublicKey(publicKeyType, publicKeySize, pubKeyOutput));
    }
  }

  public MachineShellConnectionFactory getConnectionFactory(
      MachineBuilderContext builderContext,
      MachineShellUserRepository userRepository) throws MachineBuildingException {
    MutableAddressMachineShellConectionFactory conectionFactory =
        new MutableAddressMachineShellConectionFactory(
            new MachineShellConnectionFactoryLifecycle<>(
                address -> new PoolingMachineShellConnectionFactory(new MachineShellClient(
                    clientContext,
                    address,
                    builderContext.getProperty("shellPort")
                        .stream()
                        .findAny()
                        .map(Integer::parseInt)
                        .orElse(null),
                    new MachineShellAuthorizedUsers(userRepository))),
                PoolingMachineShellConnectionFactory::cleanup));
    MachineAgent agent = builderContext.getAgent();
    MachineNetworkAddressBinding addressBinding = new MachineNetworkAddressBinding(
        builderContext.getProperty("shellNetwork")
            .stream()
            .findAny()
            .map(MachineNetworkName::new)
            .orElseThrow(() -> new MachineBuildingException("Undefined shell network")),
        conectionFactory::setAddress,
        agent.getNetworks());
    builderContext.addDisposeAction(() -> agent.removePropertyChangeListener(addressBinding));
    agent.addPropertyChangeListener(addressBinding);
    return conectionFactory;
  }

  private static String readFrom(File file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      return reader.readLine();
    }
  }
}
