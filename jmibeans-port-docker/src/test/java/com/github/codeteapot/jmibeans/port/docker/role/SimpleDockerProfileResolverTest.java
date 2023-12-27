package com.github.codeteapot.jmibeans.port.docker.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import com.github.codeteapot.jmibeans.port.docker.DockerProfileResolver;
import com.github.codeteapot.jmibeans.port.docker.role.SimpleDockerProfileResolver;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class SimpleDockerProfileResolverTest {

  private static final MachineProfileName NULL_PROFILE_NAME = null;

  private static final String SOME_ROLE = "some-role";
  private static final String ANOTHER_ROLE = "another-role";

  private static final MachineProfileName SOME_PROFILE_NAME = new MachineProfileName("some-name");
  private static final MachineProfileName ANOTHER_PROFILE_NAME = new MachineProfileName(
      "another-name");

  @Test
  public void hasDefaultProfile() {
    DockerProfileResolver resolver = new SimpleDockerProfileResolver(
        role -> NULL_PROFILE_NAME,
        SOME_PROFILE_NAME);

    MachineProfileName name = resolver.getDefault();

    assertThat(name).isEqualTo(SOME_PROFILE_NAME);
  }

  @Test
  public void mapExistingRole() {
    DockerProfileResolver resolver = new SimpleDockerProfileResolver(
        role -> role.equals(SOME_ROLE) ? SOME_PROFILE_NAME : NULL_PROFILE_NAME,
        ANOTHER_PROFILE_NAME);

    Optional<MachineProfileName> name = resolver.fromRole(SOME_ROLE);

    assertThat(name).hasValue(SOME_PROFILE_NAME);
  }

  @Test
  public void mapNonExistingRole() {
    DockerProfileResolver resolver = new SimpleDockerProfileResolver(
        role -> role.equals(SOME_ROLE) ? SOME_PROFILE_NAME : NULL_PROFILE_NAME,
        ANOTHER_PROFILE_NAME);

    Optional<MachineProfileName> name = resolver.fromRole(ANOTHER_ROLE);

    assertThat(name).isEmpty();
  }
}
