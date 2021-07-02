package kz.greetgo.cached.core.main;

import java.util.concurrent.ConcurrentHashMap;

public class CacheEngines implements CacheEngineSelector {

  private final CacheEngine defaultEngine;

  private CacheEngines(CacheEngine defaultEngine) {
    this.defaultEngine = defaultEngine;
  }

  public static CacheEngines createWithDefault(CacheEngine defaultEngine) {
    return new CacheEngines(defaultEngine);
  }

  private final ConcurrentHashMap<String, CacheEngine> store = new ConcurrentHashMap<>();

  @Override
  public CacheEngine select(String cacheEngineName) throws NoCacheEngineWithName {
    if (cacheEngineName == null) {
      return defaultEngine;
    }
    var cacheEngine = store.get(cacheEngineName);
    if (cacheEngine == null) {
      throw new NoCacheEngineWithName(cacheEngineName);
    }
    return cacheEngine;
  }

  private void put(String cacheEngineName, CacheEngine cacheEngine) {
    store.put(cacheEngineName, cacheEngine);
  }

}
