package com.github.codeteapot.jmibeans;

import com.github.codeteapot.jmibeans.session.MachineSession;

@FunctionalInterface
interface AcquireMachineSessionClause {

  MachineSession acquire(String username);
}
