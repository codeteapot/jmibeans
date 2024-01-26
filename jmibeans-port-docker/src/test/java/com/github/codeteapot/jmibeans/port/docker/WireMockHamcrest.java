package com.github.codeteapot.jmibeans.port.docker;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

class WireMockHamcrest {

  private WireMockHamcrest() {}

  static StringValuePattern wmEqualTo(String value) {
    return WireMock.equalTo(value);
  }
}
