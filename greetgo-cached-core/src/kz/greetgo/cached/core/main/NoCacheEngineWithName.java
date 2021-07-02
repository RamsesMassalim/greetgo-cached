package kz.greetgo.cached.core.main;

public class NoCacheEngineWithName extends RuntimeException {
  public NoCacheEngineWithName(String cacheEngineName) {
    super("cacheEngineName = " + cacheEngineName);
  }
}
