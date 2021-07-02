package kz.greetgo.cached.core.main;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CacheEngines implements CacheEngineSelector {

  private final AtomicReference<CacheEngine> defaultEngine = new AtomicReference<>(null);

  private CacheEngines(CacheEngine defaultEngine) {
    this.defaultEngine.set(defaultEngine);
  }

  public CacheEngines() {}

  public static CacheEngines createWithDefault(CacheEngine defaultEngine) {
    return new CacheEngines(defaultEngine);
  }

  private final ConcurrentHashMap<String, CacheEngine> store = new ConcurrentHashMap<>();

  @Override
  public CacheEngine select(String cacheEngineName) throws NoCacheEngineWithName {
    if (cacheEngineName == null) {
      var ret = defaultEngine.get();
      if (ret == null) {
        throw new NoDefaultCacheEngine();
      }
      return ret;
    }
    var cacheEngine = store.get(cacheEngineName);
    if (cacheEngine == null) {
      throw new NoCacheEngineWithName(cacheEngineName);
    }
    return cacheEngine;
  }

  public void put(String cacheEngineName, CacheEngine cacheEngine) {
    if (cacheEngineName == null) {
      defaultEngine.set(cacheEngine);
      return;
    }
    store.put(cacheEngineName, cacheEngine);
  }

}
