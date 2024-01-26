package com.github.codeteapot.jmibeans.examples.httpcert.catalog.certauth;

import com.github.codeteapot.jmibeans.examples.httpcert.catalog.MachineFacetConfigLoader;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellConnectionFactory;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class CertificateAuthorityFacetFactory {

  private static final String PROPERTIES_PATH = "/etc/jmi.properties.sh";
  private static final String CERTIFICATE_FACTORY_TYPE = "X.509";

  public static final String CERT_ISSUER_USER = "cert-issuer";

  private final MachineFacetConfigLoader<CertificateAuthorityConfig> configLoader;
  private final CertificateFactory certificateFactory;

  public CertificateAuthorityFacetFactory() throws CertificateException {
    configLoader = new MachineFacetConfigLoader<>(PROPERTIES_PATH, CertificateAuthorityConfig::new);
    certificateFactory = CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE);
  }

  public CertificateAuthorityFacet getFacet(MachineShellConnectionFactory connectionFactory)
      throws MachineBuildingException, InterruptedException {
    try (MachineShellConnection connection = connectionFactory.getConnection(CERT_ISSUER_USER)) {
      CertificateAuthorityConfig config = configLoader.load(connection);
      return new CertificateAuthorityFacet(
          connectionFactory,
          certificateFactory,
          config.getBaseDir());
    } catch (MachineShellException | MachineShellCommandExecutionException e) {
      throw new MachineBuildingException(e);
    }
  }
}
