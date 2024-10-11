package com.github.codeteapot.jmibeans.examples.httpcert;

import static com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth //
    .CertificateAuthorityFacetFactory.CERT_ISSUER_USER;
import static com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver //
    .WebServerFacetFactory.HTTP_ADMIN_USER;
import static com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacetFactory //
    .DNS_REGISTRAR_USER;
import static com.github.codeteapot.jmibeans.platform.Machine.facetFilter;
import static com.github.codeteapot.jmibeans.platform.Machine.facetGet;
import static java.lang.Runtime.getRuntime;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.MachineCatalog;
import com.github.codeteapot.jmibeans.MachineCatalogDefinition;
import com.github.codeteapot.jmibeans.PlatformAdapter;
import com.github.codeteapot.jmibeans.PlatformEventQueue;
import com.github.codeteapot.jmibeans.examples.csr.security.cert.CertificateSigningRequestException;
import com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth //
    .CertificateAuthorityException;
import com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth //
    .CertificateAuthorityFacet;
import com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth //
    .CertificateAuthorityFacetFactory;
import com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver.WebServerFacet;
import com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver.WebServerFacetFactory;
import com.github.codeteapot.jmibeans.library.dns.DNSResolver;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHostConfig;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSHostFacetFactory;
import com.github.codeteapot.jmibeans.library.dns.catalog.DNSServerFacetFactory;
import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.platform.MachineRef;
import com.github.codeteapot.jmibeans.platform.PlatformContext;
import com.github.codeteapot.jmibeans.platform.event.MachineAvailableEvent;
import com.github.codeteapot.jmibeans.platform.event.MachineLostEvent;
import com.github.codeteapot.jmibeans.platform.event.PlatformListener;
import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.PlatformPort;
import com.github.codeteapot.jmibeans.port.docker.DockerPlatformPort;
import com.github.codeteapot.jmibeans.port.docker.DockerTarget;
import com.github.codeteapot.jmibeans.port.docker.role.DirectMappingDockerProfileResolver;
import com.github.codeteapot.jmibeans.profile.MachineBuilderResultDefinition;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO RENAME Module to jmibeans-examples-webcert
public class Application implements PlatformListener {

  private static final MachineProfileName DEFAULT_PROFILE_NAME = new MachineProfileName("default");
  private static final MachineProfileName DNS_PROFILE_NAME = new MachineProfileName("dns");
  private static final MachineProfileName CERT_PROFILE_NAME = new MachineProfileName("cert");
  private static final MachineProfileName HTTP_PROFILE_NAME = new MachineProfileName("http");

  private static final Logger logger = getLogger(Application.class.getName());

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
                .flatMap(facetFilter(WebServerFacet.class))
                .filter(WebServerFacet::isCertificateEmpty)
                .forEach(webServerFacet -> issue(certAuthFacet, webServerFacet));
          });
    }
    context.lookup(event.getMachineRef())
        .flatMap(facetGet(WebServerFacet.class))
        .filter(WebServerFacet::isCertificateEmpty)
        .ifPresent(webServerFacet -> ofNullable(certAuthRef)
            .flatMap(context::lookup)
            .flatMap(facetGet(CertificateAuthorityFacet.class))
            .ifPresent(certAuthFacet -> issue(certAuthFacet, webServerFacet)));
  }

  @Override
  public void machineLost(MachineLostEvent event) {
    if (Objects.equals(certAuthRef, event.getMachineRef())) {
      context.available()
          .flatMap(facetFilter(WebServerFacet.class))
          .forEach(WebServerFacet::certificateClear);
      certAuthRef = null;
    }
  }

  private void issue(CertificateAuthorityFacet certAuthFacet, WebServerFacet webServerFacet) {
    try {
      webServerFacet.certificatePut(certAuthFacet.issue(webServerFacet.generateKeyPair()));
    } catch (CertificateSigningRequestException | CertificateAuthorityException e) {
      logger.log(WARNING, "Certificate not issued", e);
    }
  }

  public static void main(String[] args) throws Exception {
    ApplicationConfig config = new ApplicationConfig();

    PlatformEventQueue eventQueue = new PlatformEventQueue();
    PlatformAdapter adapter = new PlatformAdapter(
        eventQueue,
        catalog(
            config.getDNSZoneName(),
            new SimpleShellProvider(
                config.getShellPublicKeyRepositoryHost(),
                config.getShellPublicKeyRepositoryUser(),
                config.getShellPublicKeyRepositoryPasswordFile(),
                config.getShellPublicKeyRepositoryPath(),
                config.getShellPublicKeyType(),
                config.getShellPublicKeySize()),
            new CertificateAuthorityFacetFactory(),
            new WebServerFacetFactory(),
            new DNSHostFacetFactory(),
            new DNSServerFacetFactory()),
        newCachedThreadPool());
    eventQueue.addListener(new DNSResolver(adapter.getContext(), config.getDNSZoneName()));
    eventQueue.addListener(new Application(adapter.getContext()));

    PlatformPort dockerPort = new DockerPlatformPort(
        config.getDockerPortGroup(),
        new DockerTarget(
            config.getDockerPortTargetHost(),
            config.getDockerPortTargetPort().orElse(null)),
        config.getDockerPortEventsTimeout(),
        new DirectMappingDockerProfileResolver(DEFAULT_PROFILE_NAME)
            .withMapping(config.getDockerPortNameServerRole(), DNS_PROFILE_NAME)
            .withMapping(config.getDockerPortCertAuthRole(), CERT_PROFILE_NAME)
            .withMapping(config.getDockerPortHttpServerRole(), HTTP_PROFILE_NAME));

    ExecutorService listenExecutor = newSingleThreadExecutor();
    getRuntime().addShutdownHook(new Thread(listenExecutor::shutdownNow));
    listenExecutor.submit(() -> {
      adapter.listen(dockerPort);
      return null;
    });
    eventQueue.dispatchEvents();
  }

  private static MachineCatalog catalog(
      String dnsZoneName,
      SimpleShellProvider shellProvider,
      CertificateAuthorityFacetFactory certAuthFacetFactory,
      WebServerFacetFactory webServerFacetFactory,
      DNSHostFacetFactory dnsHostFacetFactory,
      DNSServerFacetFactory dnsServerFacetFactory) {
    return new MachineCatalogDefinition()
        .withProfile(DNS_PROFILE_NAME, builderContext -> {
          return new MachineBuilderResultDefinition()
              .withFacet(dnsServerFacetFactory.getFacet(
                  shellProvider.getConnectionFactory(
                      builderContext,
                      new StandaloneIndentityShellUser(
                          DNS_REGISTRAR_USER,
                          builderContext.getProperty("shellAdminUser")
                              .stream()
                              .findAny()
                              .orElseThrow(() -> new MachineBuildingException(
                                  "Undefined DNS admin user name"))))));
        })
        .withProfile(CERT_PROFILE_NAME, builderContext -> {
          return new MachineBuilderResultDefinition()
              .withFacet(certAuthFacetFactory.getFacet(
                  shellProvider.getConnectionFactory(
                      builderContext,
                      new StandaloneIndentityShellUser(
                          CERT_ISSUER_USER,
                          builderContext.getProperty("shellCertUser")
                              .stream()
                              .findAny()
                              .orElseThrow(() -> new MachineBuildingException(
                                  "Undefined CA user name"))))));
        })
        .withProfile(HTTP_PROFILE_NAME, builderContext -> {
          return new MachineBuilderResultDefinition()
              .withFacet(dnsHostFacetFactory.getFacet(
                  builderContext,
                  singleton(new DNSHostConfig(
                      dnsZoneName,
                      builderContext.getProperty("dnsNetwork")
                          .stream()
                          .findAny()
                          .map(MachineNetworkName::new)
                          .orElseThrow(() -> new MachineBuildingException(
                              "Undefined DNS network name")),
                      builderContext.getProperty("dnsHostName")
                          .stream()
                          .collect(Collectors.toSet())))))
              .withFacet(webServerFacetFactory.getFacet(
                  shellProvider.getConnectionFactory(
                      builderContext,
                      new StandaloneIndentityShellUser(
                          HTTP_ADMIN_USER,
                          builderContext.getProperty("shellAdminUser")
                              .stream()
                              .findAny()
                              .orElseThrow(() -> new MachineBuildingException(
                                  "Undefined HTTP admin user name"))))));
        });
  }
}
