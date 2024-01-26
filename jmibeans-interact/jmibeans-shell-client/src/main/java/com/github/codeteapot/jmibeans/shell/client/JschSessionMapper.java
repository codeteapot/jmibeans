package com.github.codeteapot.jmibeans.shell.client;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@FunctionalInterface
interface JschSessionMapper {

  Session map(String username, String host) throws JSchException;
}
