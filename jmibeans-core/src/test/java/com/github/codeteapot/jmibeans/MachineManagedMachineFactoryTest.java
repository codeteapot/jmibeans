package com.github.codeteapot.jmibeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.codeteapot.jmibeans.MachineBuilder;
import com.github.codeteapot.jmibeans.MachineBuilderContext;
import com.github.codeteapot.jmibeans.MachineFacetFactory;
import com.github.codeteapot.jmibeans.MachineSessionPoolReleaser;
import com.github.codeteapot.jmibeans.ManagedMachine;
import com.github.codeteapot.jmibeans.ManagedMachineConstructor;
import com.github.codeteapot.jmibeans.ManagedMachineFactory;
import com.github.codeteapot.jmibeans.ManagedMachineFactoryImpl;
import com.github.codeteapot.jmibeans.machine.MachineContext;
import com.github.codeteapot.jmibeans.machine.MachineFacet;
import com.github.codeteapot.jmibeans.session.MachineSession;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MachineManagedMachineFactoryTest {

  private static final String SOME_USERNAME = "some-username";

  @Mock
  private MachineContext context;

  @Mock
  private Set<MachineFacet> facets;

  @Mock
  private ManagedMachineConstructor machineConstructor;

  private ManagedMachineFactory managedFactory;

  @BeforeEach
  public void setUp() {
    managedFactory = new ManagedMachineFactoryImpl(context, facets, machineConstructor);
  }

  @Test
  public void buildItself(@Mock MachineBuilder someBuilder) throws Exception {
    managedFactory.build(someBuilder);

    InOrder order = Mockito.inOrder(facets, someBuilder);
    order.verify(facets).clear();
    order.verify(someBuilder).build((MachineBuilderContext) managedFactory);
  }

  @Test
  public void giveCreatedMachine(
      @Mock MachineSessionPoolReleaser someSessionPoolReleaser,
      @Mock ManagedMachine someMachine) throws Exception {
    when(machineConstructor.construct(someSessionPoolReleaser, facets))
        .thenReturn(someMachine);

    ManagedMachine machine = managedFactory.getMachine(someSessionPoolReleaser);

    assertThat(machine).isEqualTo(someMachine);
  }

  @Test
  public void giveSessionAsBuilderContext(@Mock MachineSession someSession) throws Exception {
    when(context.getSession(SOME_USERNAME))
        .thenReturn(someSession);

    MachineBuilderContext builderContext = (MachineBuilderContext) managedFactory;
    MachineSession session = builderContext.getSession(SOME_USERNAME);

    assertThat(session).isEqualTo(someSession);
  }

  @Test
  public void registerFacetAsBuilderContext(
      @Mock MachineFacetFactory someFacetFactory,
      @Mock MachineFacet someFacet) throws Exception {
    when(someFacetFactory.getFacet(context))
        .thenReturn(someFacet);

    MachineBuilderContext builderContext = (MachineBuilderContext) managedFactory;
    builderContext.register(someFacetFactory);

    verify(facets).add(someFacet);
  }
}
