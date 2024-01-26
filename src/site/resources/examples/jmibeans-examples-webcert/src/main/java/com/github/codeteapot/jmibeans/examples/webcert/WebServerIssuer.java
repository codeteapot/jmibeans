package com.github.codeteapot.jmibeans.examples.webcert;

import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;
import java.util.logging.Logger;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateAuthorityException;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateAuthorityFacet;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolder;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateHolderFacet;
import com.github.codeteapot.jmibeans.depot.ca.catalog.CertificateSigningRequestException;
import com.github.codeteapot.jmibeans.examples.webcert.catalog.WebServerFacet;

class WebServerIssuer {

  private static final Logger logger = getLogger(WebServerIssuer.class.getName());

  private CertificateHolderFacet certHolderFacet;
  private WebServerFacet webServerFacet;

  WebServerIssuer() {
    certHolderFacet = null;
    webServerFacet = null;
  }

  WebServerIssuer inject(CertificateHolderFacet certHolderFacet) {
    this.certHolderFacet = certHolderFacet;
    return this;
  }

  WebServerIssuer inject(WebServerFacet webServerFacet) {
    this.webServerFacet = webServerFacet;
    return this;
  }

  void issue(CertificateAuthorityFacet certAuthFacet) {
    certHolderFacet.getHolders().forEach(certHolder -> issue(certAuthFacet, certHolder));
  }

  void disable() {
    certHolderFacet.getHolders().forEach(this::disable);
  }

  private void issue(CertificateAuthorityFacet certAuthFacet, CertificateHolder certHolder) {
    try {
      certHolder.put(certAuthFacet.issue(certHolder.generateKeyPair()));
      webServerFacet.enableSite();
    } catch (CertificateSigningRequestException | CertificateAuthorityException e) {
      logger.log(WARNING, "Certificate not issued", e);
    }
  }

  private void disable(CertificateHolder certHolder) {
    certHolder.clear();
    webServerFacet.disableSite();
  }
}
