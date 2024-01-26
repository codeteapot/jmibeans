package com.github.codeteapot.jmibeans.port.docker;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import java.util.logging.Logger;

class DockerEventsResultCallback extends ResultCallback.Adapter<Event> {

  private static final Logger logger = getLogger(DockerEventsResultCallback.class.getName());

  private static final String ACTION_START = "start";
  private static final String ACTION_DIE = "die";

  private final DockerPlatformPortForwarder forwarder;

  DockerEventsResultCallback(DockerPlatformPortForwarder forwarder) {
    this.forwarder = requireNonNull(forwarder);
  }

  @Override
  public void onNext(Event event) {
    switch (event.getAction()) {
      case ACTION_START:
        forwarder.accept(event.getId());
        break;
      case ACTION_DIE:
        forwarder.forget(event.getId());
        break;
      default:
        logger.fine(new StringBuilder()
            .append("Ignoring event with action ")
            .append(event.getAction())
            .toString());
    }
  }
}
