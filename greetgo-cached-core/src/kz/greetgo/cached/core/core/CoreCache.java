package kz.greetgo.cached.core.core;

import java.util.Map;
import java.util.function.Supplier;

public interface CoreCache<In, Out> {

  Out get(In in, Supplier<Out> direct);

  Map<String, Object> params();

  void invalidateAll();

  void invalidateOn(In in);

  void close();
}
