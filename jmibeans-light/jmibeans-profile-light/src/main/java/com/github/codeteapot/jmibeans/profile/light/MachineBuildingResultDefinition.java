package com.github.codeteapot.jmibeans.profile.light;

import java.util.HashSet;
import java.util.Set;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.MachineBuildingResult;

public class MachineBuildingResultDefinition implements MachineBuildingResult {

  private final Set<Object> facets;

  public MachineBuildingResultDefinition() {
    facets = new HashSet<>();
  }

  @Override
  public Set<Object> getFacets() {
    return facets;
  }

  public <S> Scope<S> initial(S scope) {
    return new MachineBuidingScopeDefinition<>(this, scope);
  }

  public Scope<Void> initial() {
    return new MachineBuidingScopeDefinition<>(this, null);
  }

  void addFacet(Object facet) {
    facets.add(facet);
  }

  public interface Scope<S> extends MachineBuildingResult {

    <SR> Scope<SR> map(ScopeFunction<S, SR> mapper)
        throws MachineBuildingException, InterruptedException;

    Scope<S> peek(ScopeConsumer<S> receiver) throws MachineBuildingException, InterruptedException;

    <F> Scope<S> register(ScopeFunction<S, F> facetMapper)
        throws MachineBuildingException, InterruptedException;
  }

  public interface ScopeConsumer<S> {

    void accept(S scope) throws MachineBuildingException, InterruptedException;
  }

  public interface ScopeFunction<S, SR> {

    SR apply(S scope) throws MachineBuildingException, InterruptedException;
  }
}
