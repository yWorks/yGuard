package com.yworks.util.graph;

/**
 * An immutable pair representing the two endpoints of an edge in a graph.
 */
public class EndpointPair<N> {
  private final N source;
  private final N target;

  EndpointPair( N source, N target ) {
    this.source = source;
    this.target = target;
  }

  public N source() {
    return source;
  }

  public N target() {
    return target;
  }

  @Override
  public boolean equals( final Object other ) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    final EndpointPair<?> pair = (EndpointPair<?>) other;

    if (source != null ? !source.equals(pair.source) : pair.source != null) {
      return false;
    }
    if (target != null ? !target.equals(pair.target) : pair.target != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = source != null ? source.hashCode() : 0;
    result = 31 * result + (target != null ? target.hashCode() : 0);
    return result;
  }
}
