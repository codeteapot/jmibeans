package com.github.codeteapot.jmibeans.examples.httpcert.catalog.webserver;

import com.github.codeteapot.jmibeans.examples.httpcert.catalog.MachineFacetConfigLoader;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;

public class WebServerFacetFactory {

  private static final String PROPERTIES_PATH = "/etc/jmi.properties.sh";

  public static final String HTTP_ADMIN_USER = "http-admin";

  private final MachineFacetConfigLoader<WebServerConfig> configLoader;

  public WebServerFacetFactory() {
    configLoader = new MachineFacetConfigLoader<>(PROPERTIES_PATH, WebServerConfig::new);
  }

  public WebServerFacet getFacet(MachineShellConnectionFactory connectionFactory)
      throws MachineBuildingException {
    try (MachineShellConnection connection = connectionFactory.getConnection(HTTP_ADMIN_USER)) {
      WebServerConfig config = configLoader.load(connection);
      return new WebServerFacet(
          connectionFactory,
          new GenerateKeyPairCommandFactory(
              config.getPrivateKeyAlgorithm(),
              config.getPrivateKeySize()),
          config.getServerName(),
          config.getSiteName(),
          config.getCertificatePath(),
          config.getPrivateKeyPath());
    } catch (MachineShellException | MachineShellCommandExecutionException e) {
      throw new MachineBuildingException(e);
    }
  }
}
