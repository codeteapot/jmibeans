package com.github.codeteapot.jmibeans;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import com.github.codeteapot.jmibeans.profile.MachineBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MachineBuildingJobTest {

  @Mock
  private MachineBuilder builder;

  private ManagedMachineBuildingJob buildingJob;

  @BeforeEach
  void setUp() {
    buildingJob = new ManagedMachineBuildingJob(newSingleThreadExecutor(), builder);
  }

  @Test
  void submit(@Mock ManagedMachineBuildingJobAction someAction) {
    buildingJob.submit(someAction);

    await().untilAsserted(() -> verify(someAction).build(builder));
  }
}
