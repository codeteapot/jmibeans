package com.github.codeteapot.jmibeans.port.docker.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.port.MachineProfileName;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirectMappingDockerProfileResolverTest {

  private static final String SOME_ROLE = "some-role";
  private static final String ANOTHER_ROLE = "another-role";

  private static final MachineProfileName DEFAULT_PROFILE_NAME = new MachineProfileName("default");
  private static final MachineProfileName SOME_PROFILE = new MachineProfileName("some");

  private DirectMappingDockerProfileResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new DirectMappingDockerProfileResolver(DEFAULT_PROFILE_NAME);
  }

  @Test
  void hasDefault() {
    MachineProfileName profileName = resolver.getDefault();

    assertThat(profileName).isEqualTo(DEFAULT_PROFILE_NAME);
  }

  @Test
  void mapFromKnowRole() {
    resolver.profileMap.put(SOME_ROLE, SOME_PROFILE);

    Optional<MachineProfileName> profileName = resolver.fromRole(SOME_ROLE);

    assertThat(profileName).hasValue(SOME_PROFILE);
  }

  @Test
  void mapFromUnknowRole() {
    resolver.profileMap.put(SOME_ROLE, SOME_PROFILE);

    Optional<MachineProfileName> profileName = resolver.fromRole(ANOTHER_ROLE);

    assertThat(profileName).isEmpty();
  }

  @Test
  void addMapping() {
    DirectMappingDockerProfileResolver chain = resolver.withMapping(SOME_ROLE, SOME_PROFILE);

    assertThat(chain).isEqualTo(resolver);
    assertThat(resolver.profileMap).hasSize(1).containsEntry(SOME_ROLE, SOME_PROFILE);
  }
}
