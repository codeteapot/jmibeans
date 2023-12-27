package com.github.codeteapot.jmibeans;

import static com.github.codeteapot.jmibeans.Machine.facetFilter;
import static com.github.codeteapot.jmibeans.Machine.facetGet;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.Machine;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class MachineTest {

  @Test
  public void existingFacetGetByType() {
    TestFooMachineFacet someFooFacet = new TestFooMachineFacet();
    Machine machine = new TestMachine(someFooFacet);

    Optional<TestFooMachineFacet> facet = Optional.of(machine)
        .flatMap(facetGet(TestFooMachineFacet.class));

    assertThat(facet).hasValue(someFooFacet);
  }

  @Test
  public void nonExistingFacetGetByType() {
    TestFooMachineFacet someFooFacet = new TestFooMachineFacet();
    Machine machine = new TestMachine(someFooFacet);

    Optional<TestBarMachineFacet> facet = Optional.of(machine)
        .flatMap(facetGet(TestBarMachineFacet.class));

    assertThat(facet).isEmpty();
  }

  @Test
  public void existingFacetFilterByType() {
    TestFooMachineFacet someFooFacet = new TestFooMachineFacet();
    Machine machine = new TestMachine(someFooFacet);

    Optional<TestFooMachineFacet> facet = Stream.of(machine)
        .flatMap(facetFilter(TestFooMachineFacet.class))
        .findAny();

    assertThat(facet).hasValue(someFooFacet);
  }

  @Test
  public void nonExistingFacetFilterByType() {
    TestFooMachineFacet someFooFacet = new TestFooMachineFacet();
    Machine machine = new TestMachine(someFooFacet);

    Optional<TestBarMachineFacet> facet = Stream.of(machine)
        .flatMap(facetFilter(TestBarMachineFacet.class))
        .findAny();

    assertThat(facet).isEmpty();
  }
}
