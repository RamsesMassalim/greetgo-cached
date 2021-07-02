package kz.greetgo.cached.core.main;

import java.util.Map;
import java.util.function.Supplier;

public class CoreCacheEmpty<In, Out> implements CoreCache<In, Out> {

  private final Map<String, Object> cacheParams;

  public CoreCacheEmpty(Map<String, Object> cacheParams) {
    this.cacheParams = cacheParams;
  }

  @Override
  public Out get(In in, Supplier<Out> direct) {
    return direct.get();
  }

  @Override
  public Map<String, Object> params() {
    return cacheParams;
  }

  @Override
  public void invalidateAll() {
    // do nothing
  }

  @Override
  public void invalidateOn(In in) {
    // do nothing
  }

  @Override
  public void close() {
    // do nothing
  }
}
