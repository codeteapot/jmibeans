package com.github.codeteapot.jmibeans;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperties;
import com.github.codeteapot.jmibeans.port.MachineBuilderProperty;
import com.github.codeteapot.jmibeans.profile.MachineBuilder;

@ExtendWith(MockitoExtension.class)
class MachineBuildingJobTest {

  private static final String SOME_BUILDER_PROPERTY_NAME = "someBuilderProperty";
  private static final String SOME_BUILDER_PROPERTY_RAW_VALUE = "someBuilderPropertyRawValue";

  @Mock
  private MachineContainerBuilderPropertyConverters builderPropertyConverters;

  @Mock
  private Iterator<MachineBuilderProperty> builderPropertiesIterator;

  @Mock
  private MachineBuilder builder;

  @Mock
  private ManagedMachineBuilderPropertyValueConstructor builderPropertyValueConstructor;

  private ManagedMachineBuildingJob buildingJob;

  @BeforeEach
  void setUp(@Mock MachineBuilderProperties builderProperties) {
    when(builderProperties.iterator()).thenReturn(builderPropertiesIterator);

    buildingJob = new ManagedMachineBuildingJob(
        builderPropertyConverters,
        newSingleThreadExecutor(),
        builderProperties,
        builder,
        builderPropertyValueConstructor);
  }

  @Test
  void submit(
      @Mock ManagedMachineBuildingJobAction someAction,
      @Mock ManagedMachineBuilderPropertyValue someBuilderPropertyValue) {
    when(builderPropertyValueConstructor.construct(
        builderPropertyConverters,
        SOME_BUILDER_PROPERTY_RAW_VALUE)).thenReturn(someBuilderPropertyValue);
    when(builderPropertiesIterator.hasNext()).thenReturn(true, false);
    when(builderPropertiesIterator.next()).thenReturn(new MachineBuilderProperty(
        SOME_BUILDER_PROPERTY_NAME,
        SOME_BUILDER_PROPERTY_RAW_VALUE));

    buildingJob.submit(someAction);

    InOrder order = inOrder(builder, someAction);
    await().untilAsserted(() -> {
      order.verify(builder).setProperty(SOME_BUILDER_PROPERTY_NAME, someBuilderPropertyValue);
      order.verify(someAction).build(builder);
    });
  }
}
