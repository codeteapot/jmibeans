package com.github.codeteapot.jmibeans.examples.webcert;

import static com.github.codeteapot.jmibeans.platform.Machine.facetGet;
import static com.github.codeteapot.jmibeans.platform.light.MachineFacetComposer.composeFacet;
import static com.github.codeteapot.jmibeans.platform.light.MachineFacetComposer.composeFilter;
import static com.github.codeteapot.jmibeans.platform.light.MachineFacetComposer.composeGet;
import static com.github.codeteapot.jmibeans.platform.light.MachineFacetComposer.composeMap;
import static com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory.DEFAULT_USERNAME;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.getenv;
import static java.nio.file.Files.newOutputStream;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.function.Function.identity;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import com.github.codeteapot.jmibeans.PlatformAdapter;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateAuthorityFacet;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolderExtensions;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolderFacet;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolderName;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolderSettings;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolderSubject;
import com.github.codeteapot.jmibeans.depot.dns.DNSLayout;
import com.github.codeteapot.jmibeans.depot.dns.DNSLayoutZone;
import com.github.codeteapot.jmibeans.depot.dns.DNSResolver;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSHostFacet;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSServerFacet;
import com.github.codeteapot.jmibeans.depot.dns.catalog.DNSZoneSettings;
import com.github.codeteapot.jmibeans.examples.webcert.catalog.WebServerFacet;
import com.github.codeteapot.jmibeans.light.MachineCatalogDefinition;
import com.github.codeteapot.jmibeans.light.PlatformEventQueue;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.PlatformPortFactory;
import com.github.codeteapot.jmibeans.port.light.PlatformPortsLoader;
import com.github.codeteapot.jmibeans.profile.light.MachineBuildingResultDefinition;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClient;
import com.github.codeteapot.jmibeans.shell.client.MachineShellClientContext;
import com.github.codeteapot.jmibeans.shell.client.MachineShellPublicKey;
import com.github.codeteapot.jmibeans.shell.client.pool.PoolingMachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.client.security.auth.MachineShellIdentityName;
import com.github.codeteapot.jmibeans.shell.client.security.auth.host.MachineShellHost;
import com.github.codeteapot.jmibeans.shell.client.security.auth.host.MachineShellKnownHosts;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellAuthorizedUsers;
import com.github.codeteapot.jmibeans.shell.client.secutity.auth.user.MachineShellUser;
import com.github.codeteapot.jmibeans.shell.provider.MachineShellConnectionFactoryLifecycle;
import com.github.codeteapot.jmibeans.shell.provider.MachineShellProvider;

public class Application implements PlatformListener {

  private final PlatformContext context;
  private MachineRef certAuthRef;

  private Application(PlatformContext context) {
    this.context = requireNonNull(context);
    certAuthRef = null;
  }

  @Override
  public void machineAvailable(MachineAvailableEvent event) {
    if (certAuthRef == null) {
      context.lookup(event.getMachineRef())
          .flatMap(facetGet(CertificateAuthorityFacet.class))
          .ifPresent(certAuthFacet -> {
            certAuthRef = event.getMachineRef();
            context.available()
                .map(composeFacet(WebServerIssuer::new))
                .map(composeMap(WebServerIssuer::inject, CertificateHolderFacet.class))
                .map(composeMap(WebServerIssuer::inject, WebServerFacet.class))
                .flatMap(composeFilter(identity()))
                .forEach(issuer -> issuer.issue(certAuthFacet));
          });
    }
    context.lookup(event.getMachineRef())
        .map(composeFacet(WebServerIssuer::new))
        .map(composeMap(WebServerIssuer::inject, CertificateHolderFacet.class))
        .map(composeMap(WebServerIssuer::inject, WebServerFacet.class))
        .flatMap(composeGet(identity()))
        .ifPresent(issuer -> ofNullable(certAuthRef)
            .flatMap(context::lookup)
            .flatMap(facetGet(CertificateAuthorityFacet.class))
            .ifPresent(certAuthFacet -> issuer.issue(certAuthFacet)));
  }

  @Override
  public void machineLost(MachineLostEvent event) {
    if (Objects.equals(certAuthRef, event.getMachineRef())) {
      context.available()
          .map(composeFacet(WebServerIssuer::new))
          .map(composeMap(WebServerIssuer::inject, CertificateHolderFacet.class))
          .map(composeMap(WebServerIssuer::inject, WebServerFacet.class))
          .flatMap(composeFilter(identity()))
          .forEach(issuer -> issuer.disable());
      certAuthRef = null;
    }
  }

  public static void main(String[] args) throws Exception {
    ApplicationSettings settings = new ApplicationSettings();

    Map<InetAddress, MachineShellHost> shellKnownHostsMap = new HashMap<>();
    MachineShellClientContext shellClientContext = new MachineShellClientContext(
        new MachineShellKnownHosts(
            anyHost -> true,
            shellKnownHostsMap::get,
            shellKnownHostsMap::put));
    try (OutputStream pubKeyOutput = newOutputStream(settings.getShellPublicKeyPath())) {
      shellClientContext.generateIdentity(
          new MachineShellIdentityName("any"),
          new MachineShellPublicKey(
              settings.getShellPublicKeyType(),
              settings.getShellPublicKeySize(),
              pubKeyOutput));
    }

    PlatformEventQueue eventQueue = new PlatformEventQueue();
    PlatformAdapter adapter = new PlatformAdapter(
        eventQueue,
        new MachineCatalogDefinition()
            .profile(fromEnvironment("JMI_PROFILE_DNS"))
            .inline(DNSMachineBuilderProperties::new)
            .using(builderProperties -> builderContext -> new MachineBuildingResultDefinition()
                .initial(new MachineShellProvider(
                    builderContext,
                    builderProperties.getShell().getNetworkName(),
                    newShellConnectionFactoryLifecycle(
                        shellClientContext,
                        builderProperties.getShell().getPort().orElse(null),
                        builderProperties.getShell().getUser())))
                .map(MachineShellProvider::getConnectionFactory)
                .register(connectionFactory -> new DNSServerFacet(
                    builderContext,
                    connectionFactory,
                    builderProperties.getDNSZonesScript(),
                    singleton(new DNSZoneSettings(
                        builderProperties.getDNSZoneName(),
                        builderProperties.getDNSNetworkName())))))
            .profile(fromEnvironment("JMI_PROFILE_CERT"))
            .inline(CertMachineBuilderProperties::new)
            .using(builderProperties -> builderContext -> new MachineBuildingResultDefinition()
                .initial(new MachineShellProvider(
                    builderContext,
                    builderProperties.getShell().getNetworkName(),
                    newShellConnectionFactoryLifecycle(
                        shellClientContext,
                        builderProperties.getShell().getPort().orElse(null),
                        builderProperties.getShell().getUser())))
                .map(MachineShellProvider::getConnectionFactory)
                .register(connectionFactory -> new CertificateAuthorityFacet(
                    connectionFactory,
                    builderProperties.getCABaseDir())))
            .profile(fromEnvironment("JMI_PROFILE_WEB"))
            .inline(WebMachineBuilderProperties::new)
            .using(builderProperties -> builderContext -> new MachineBuildingResultDefinition()
                .initial(new MachineShellProvider(
                    builderContext,
                    builderProperties.getShell().getNetworkName(),
                    newShellConnectionFactoryLifecycle(
                        shellClientContext,
                        builderProperties.getShell().getPort().orElse(null),
                        builderProperties.getShell().getUser())))
                .map(MachineShellProvider::getConnectionFactory)
                .register(connectionFactory -> new DNSHostFacet(
                    builderContext,
                    singleton(builderProperties.getDNSHostName())))
                .register(connectionFactory -> new CertificateHolderFacet(
                    connectionFactory,
                    singleton(new CertificateHolderSettings(new CertificateHolderName("web-cert"))
                        .withSubject(
                            new CertificateHolderSubject(builderProperties.getServerName()))
                        .withPrivateKeyAlgorithm(builderProperties.getWebPrivateKeyAlgorithm())
                        .withPrivateKeySize(builderProperties.getWebPrivateKeySize())
                        .withNodes(true)
                        .withExtensions(new CertificateHolderExtensions()
                            .withConfigSection(builderProperties.getWebCertExtConfigSection())
                            .withSubjectAltName(format("DNS:%s", builderProperties.getServerName()))
                            .withCertificatePolicies("1.2.3.4"))
                        .withCertificatePath(builderProperties.getWebCertificatePath())
                        .withPrivateKeyPath(builderProperties.getWebPrivateKeyPath()))))
                .register(connectionFactory -> new WebServerFacet(
                    connectionFactory,
                    builderProperties.getWebAvailableConfDir(),
                    builderProperties.getServerName(),
                    builderProperties.getWebSiteName()))),
        newCachedThreadPool());
    eventQueue.addListener(new DNSResolver(
        adapter.getContext(),
        new DNSLayout(singleton(new DNSLayoutZone(settings.getDNSZoneName())))));
    eventQueue.addListener(new Application(adapter.getContext()));

    PlatformPortsLoader portsLoader = new PlatformPortsLoader(
        ServiceLoader.load(PlatformPortFactory.class),
        settings.getPortsProperties());
    ExecutorService listenExecutor = newSingleThreadExecutor();
    getRuntime().addShutdownHook(new Thread(listenExecutor::shutdownNow));
    portsLoader.forEach(port -> listenExecutor.submit(() -> {
      adapter.listen(port);
      return null;
    }));
    eventQueue.dispatchEvents();
  }

  private static MachineProfileName fromEnvironment(String envName) {
    return ofNullable(getenv(envName))
        .map(MachineProfileName::new)
        .orElseThrow(() -> new IllegalArgumentException(
            format("Profile name environment variable %s is not set", envName)));
  }

  private static MachineShellConnectionFactoryLifecycle<?> newShellConnectionFactoryLifecycle(
      MachineShellClientContext clientContext,
      Integer port,
      MachineShellUser user) {
    return new MachineShellConnectionFactoryLifecycle<>(
        address -> new PoolingMachineShellConnectionFactory(new MachineShellClient(
            clientContext,
            address,
            port,
            new MachineShellAuthorizedUsers(
                username -> username.equals(DEFAULT_USERNAME) ? user : null,
                passwordName -> null))),
        PoolingMachineShellConnectionFactory::cleanup);
  }
}
