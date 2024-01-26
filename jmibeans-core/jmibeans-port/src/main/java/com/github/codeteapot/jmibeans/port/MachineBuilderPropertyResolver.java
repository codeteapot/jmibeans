package com.github.codeteapot.jmibeans.port;

import java.util.Set;

public interface MachineBuilderPropertyResolver {

  Set<String> getProperty(String name);
}
