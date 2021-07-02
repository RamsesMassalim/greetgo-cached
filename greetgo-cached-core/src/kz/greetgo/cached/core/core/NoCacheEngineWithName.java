package kz.greetgo.cached.core.core;

public class NoCacheEngineWithName extends RuntimeException {
  public NoCacheEngineWithName(String cacheEngineName) {
    super("cacheEngineName = " + cacheEngineName);
  }
}
