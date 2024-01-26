package com.github.codeteapot.jmibeans.profile.light;

import static java.util.Objects.requireNonNull;
import java.util.Set;
import com.github.codeteapot.jmibeans.profile.MachineBuildingException;
import com.github.codeteapot.jmibeans.profile.light.MachineBuildingResultDefinition.Scope;
import com.github.codeteapot.jmibeans.profile.light.MachineBuildingResultDefinition.ScopeConsumer;
import com.github.codeteapot.jmibeans.profile.light.MachineBuildingResultDefinition.ScopeFunction;

class MachineBuidingScopeDefinition<S> implements MachineBuildingResultDefinition.Scope<S> {

  private final MachineBuildingResultDefinition parent;
  private final S self;

  MachineBuidingScopeDefinition(MachineBuildingResultDefinition parent, S self) {
    this.parent = requireNonNull(parent);
    this.self = requireNonNull(self);
  }

  @Override
  public Set<Object> getFacets() {
    return parent.getFacets();
  }

  @Override
  public <SR> Scope<SR> map(ScopeFunction<S, SR> mapper)
      throws MachineBuildingException, InterruptedException {
    return new MachineBuidingScopeDefinition<>(parent, mapper.apply(self));
  }

  @Override
  public Scope<S> peek(ScopeConsumer<S> receiver)
      throws MachineBuildingException, InterruptedException {
    receiver.accept(self);
    return new MachineBuidingScopeDefinition<>(parent, self);
  }

  @Override
  public <F> Scope<S> register(ScopeFunction<S, F> facetMapper)
      throws MachineBuildingException, InterruptedException {
    parent.addFacet(facetMapper.apply(self));
    return new MachineBuidingScopeDefinition<>(parent, self);
  }
}
