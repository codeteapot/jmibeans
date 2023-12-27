package com.github.codeteapot.jmibeans.port.docker.role;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.docker.DockerProfileResolver;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation for the functional definition of role mapping.
 *
 * <p>Example with {@link Map},
 * <pre>
 * Map&lt;String, MachineProfileName&gt; profileMap = new HashMap&lt;&gt;();
 * profileMap.put("lb", new MachineProfileName("http"));
 * profileMap.put("app", new MachineProfileName("java"));
 * new SingleDockerProfileResolver(profileMap::get, new MachineProfileName("server"));
 * </pre>
 */
public class SimpleDockerProfileResolver implements DockerProfileResolver {

  private final Function<String, MachineProfileName> mapper;
  private final MachineProfileName defaultProfile;

  /**
   * Resolver with mapping function and default profile name.
   *
   * @param mapper Mapping function.
   * @param defaultProfile Default profile name.
   */
  public SimpleDockerProfileResolver(
      Function<String, MachineProfileName> mapper,
      MachineProfileName defaultProfile) {
    this.mapper = requireNonNull(mapper);
    this.defaultProfile = requireNonNull(defaultProfile);
  }

  @Override
  public MachineProfileName getDefault() {
    return defaultProfile;
  }

  @Override
  public Optional<MachineProfileName> fromRole(String roleName) {
    return ofNullable(mapper.apply(roleName));
  }
}
