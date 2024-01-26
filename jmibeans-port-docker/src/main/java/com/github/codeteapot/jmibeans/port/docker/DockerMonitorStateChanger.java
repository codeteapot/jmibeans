package com.github.codeteapot.jmibeans.port.docker;

interface DockerMonitorStateChanger {

  void changeState(DockerMonitorState newState);
}
