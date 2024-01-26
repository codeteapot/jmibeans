package com.github.codeteapot.jmibeans.session;

import com.jcraft.jsch.JSch;

@FunctionalInterface
interface SSHCredentialRepositoryConstructor {

  SSHCredentialRepository construct(JSch jsch);
}
