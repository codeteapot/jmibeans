package com.github.codeteapot.jmibeans;

/**
 * Responsible for registering facets during the creation of a machine.
 *
 * <p>Each machine profile determines the builder that will be used during creation.
 *
 * @see MachineProfile#getBuilder()
 */
public interface MachineBuilder {
  
  /**
   * The method that is called during creation.
   *
   * <p>During the execution of this method, the factories with which the facets of the machine are
   * instantiated are registered through the creation context. In this context it is possible to
   * establish sessions to interact with the machine that is created.
   *
   * <p>Example,
   * <pre>
   * try (MachineSession session = context.getSession("scott")) {
   *   context.register(new SomeMachineFacetFactory(session.execute(new GetHomeDirCommand()));
   * } catch (UnknownUserException
   *     | MachineSessionHostResolutionException
   *     | MachineSessionException e) {
   *   throw new MachineBuildingException(e);
   * }
   * </pre>
   *
   * <p>This is a blocking operation. It may be necessary to wait for the machine to be ready to
   * interact with it.
   *
   * @param context Context in which the machine is created.
   *
   * @throws MachineBuildingException In case of an error during the creation process.
   * @throws InterruptedException If the thread is interrupted during the execution of this method.
   */
  void build(MachineBuilderContext context) throws MachineBuildingException, InterruptedException;
}
