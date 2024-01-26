package com.github.codeteapot.jmibeans.examples.httpcert.catalog;

import static java.util.Objects.requireNonNull;

import com.github.codeteapot.jmibeans.shell.MachineShellCommandExecutionException;
import com.github.codeteapot.jmibeans.shell.MachineShellConnection;
import com.github.codeteapot.jmibeans.shell.MachineShellException;
import java.util.Properties;
import java.util.function.Function;

public class MachineFacetConfigLoader<C> {

  private final String propertiesPath;
  private final Function<Properties, C> configMapper;

  public MachineFacetConfigLoader(String propertiesPath, Function<Properties, C> configMapper) {
    this.propertiesPath = requireNonNull(propertiesPath);
    this.configMapper = requireNonNull(configMapper);
  }

  public C load(MachineShellConnection connection)
      throws MachineShellException, MachineShellCommandExecutionException {
    return configMapper.apply(connection.execute(new PropertiesLoadCommand(propertiesPath)));
  }
}
