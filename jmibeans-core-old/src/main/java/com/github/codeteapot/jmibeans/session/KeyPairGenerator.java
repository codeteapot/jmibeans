package com.github.codeteapot.jmibeans.session;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

@FunctionalInterface
interface KeyPairGenerator {

  KeyPair generate(JSch jsch, int type, int size) throws JSchException;
}
