package com.github.codeteapot.jmibeans.port.docker;

import static com.github.dockerjava.api.model.EventType.CONTAINER;
import static com.github.dockerjava.api.model.EventType.NETWORK;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.logging.Logger.getLogger;

import com.github.codeteapot.jmibeans.machine.MachineNetworkName;
import com.github.codeteapot.jmibeans.port.MachineManager;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetworkSettings;
import com.github.dockerjava.api.model.Event;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

class DockerController extends ResultCallback.Adapter<Event> {

  private static final String ACTION_CREATE = "create";
  private static final String ACTION_DESTROY = "destroy";
  private static final String ACTION_RESTART = "restart";
  private static final String ACTION_START = "start";
  private static final String ACTION_STOP = "stop";
  private static final String ACTION_CONNECT = "connect";
  private static final String ACTION_DISCONNECT = "disconnect";

  private static final Logger logger = getLogger(DockerController.class.getName());

  private final MachineManager machineManager;
  private final DockerProfileResolver profileResolver;
  private final DockerContainerMapper containerMapper;
  private final Map<DockerMonitorId, DockerMonitor> monitorMap;

  DockerController(
      MachineManager machineManager,
      DockerProfileResolver profileResolver,
      DockerContainerMapper containerMapper) {
    this.machineManager = requireNonNull(machineManager);
    this.profileResolver = requireNonNull(profileResolver);
    this.containerMapper = requireNonNull(containerMapper);
    monitorMap = new HashMap<>();
  }

  void init(Collection<Container> existingContainers) {
    existingContainers.forEach(container -> {
      DockerMonitorId monitorId = new DockerMonitorId(container.getId());
      DockerMonitor monitor = new DockerMonitor(monitorId, machineManager);
      monitorMap.put(monitorId, monitor);
      if ("running".equals(container.getState())) {
        monitor.start(new DockerMonitorStartContext(profileResolver, container));
      }
    });
  }

  @Override
  public void onNext(Event event) {
    DockerMonitorId monitorId = new DockerMonitorId(event.getId());
    switch (event.getType()) {
      case CONTAINER:
        switch (event.getAction()) {
          case ACTION_CREATE:
            if (monitorMap.containsKey(monitorId)) {
              logger.severe(() -> new StringBuilder()
                  .append("Monitor for container ").append(event.getId())
                  .append(" was already created")
                  .toString());
            } else {
              monitorMap.put(monitorId, new DockerMonitor(monitorId, machineManager));
            }
            break;
          case ACTION_DESTROY:
            ofNullable(monitorMap.remove(monitorId))
                .map(runnable(DockerMonitor::destroy))
                .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                    .append("Monitor for container ").append(event.getId())
                    .append(" was not created")
                    .toString())))
                .run();
            break;
          case ACTION_RESTART:
            ofNullable(monitorMap.get(monitorId))
                .map(runnable(monitor -> {
                  monitor.stop();
                  containerMapper.map(event.getId())
                      // PRE Is running
                      .map(runnable(container -> monitor.start(new DockerMonitorStartContext(
                          profileResolver,
                          container))))
                      .orElseGet(runnable(() -> logger.warning(() -> new StringBuilder()
                          .append("Container ").append(event.getId())
                          .append(" was started but it does not exist")
                          .toString())))
                      .run();
                }))
                .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                    .append("Monitor for container ").append(event.getId())
                    .append(" could not be restarted because it was not created")
                    .toString())))
                .run();
            break;
          case ACTION_START:
            ofNullable(monitorMap.get(monitorId))
                .map(monitor -> containerMapper.map(event.getId())
                    // PRE "running".equals(container.getStatus())
                    .map(runnable(container -> monitor.start(new DockerMonitorStartContext(
                        profileResolver,
                        container))))
                    .orElseGet(runnable(() -> logger.warning(() -> new StringBuilder()
                        .append("Container ").append(event.getId())
                        .append(" was started but it does not exist")
                        .toString()))))
                .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                    .append("Monitor for container ").append(event.getId())
                    .append(" could not start because it was not created")
                    .toString())))
                .run();
            break;
          case ACTION_STOP:
            ofNullable(monitorMap.get(monitorId))
                .map(runnable(DockerMonitor::stop))
                .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                    .append("Monitor for container ").append(event.getId())
                    .append(" could not stop because it was not created")
                    .toString())))
                .run();
            break;
          default:
            logger.finest(new StringBuilder()
                .append("Ignoring action ").append(event.getAction())
                .append(" for event of type ").append(CONTAINER)
                .toString());
            break;
        }
        break;
      case NETWORK:
        String networkName = event.getActor().getAttributes().get("name");
        switch (event.getAction()) {
          case ACTION_CONNECT:
            ofNullable(monitorMap.get(monitorId))
                .map(monitor -> containerMapper.map(event.getId())
                    .map(Container::getNetworkSettings)
                    .map(ContainerNetworkSettings::getNetworks)
                    .map(networks -> ofNullable(networks.get(networkName))
                        .map(network -> new DockerMachineNetwork(networkName, network))
                        .map(runnable(monitor::connect))
                        .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                            .append("Monitor for container ").append(event.getId())
                            .append(" could not be connected to the nonexistent network ")
                            .append(networkName)
                            .toString()))))
                    .orElseGet(runnable(() -> logger.warning(() -> new StringBuilder()
                        .append("Container ").append(event.getId())
                        .append(" was connected to the network ").append(networkName)
                        .append(" but it does not exist")
                        .toString()))))
                .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                    .append("Monitor for container ").append(event.getId())
                    .append(" could not be connected to the network ").append(networkName)
                    .append(" because it was not created")
                    .toString())))
                .run();
            break;
          case ACTION_DISCONNECT:
            ofNullable(monitorMap.get(monitorId))
                .map(runnable(monitor -> monitor.disconnect(new MachineNetworkName(networkName))))
                .orElseGet(runnable(() -> logger.severe(() -> new StringBuilder()
                    .append("Monitor for container ").append(event.getId())
                    .append(" could not be disconnected from the network ").append(networkName)
                    .append(" because it was not created")
                    .toString())))
                .run();
            break;
          default:
            logger.finest(new StringBuilder()
                .append("Ignoring action ").append(event.getAction())
                .append(" for event of type ").append(NETWORK)
                .toString());
            break;
        }
        break;
      default:
        logger.finest(new StringBuilder()
            .append("Ignoring event of type ").append(event.getType().getValue())
            .toString());
    }
  }

  private static <T> Function<T, Runnable> runnable(Consumer<T> action) {
    return obj -> () -> action.accept(obj);
  }

  private static Supplier<Runnable> runnable(Runnable action) {
    return () -> action;
  }
}
