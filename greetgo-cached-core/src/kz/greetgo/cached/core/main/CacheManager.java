package kz.greetgo.cached.core.main;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CacheManager {

  private final CacheSrc cacheSrc;

  public CacheManager(CacheSrc cacheSrc) {
    this.cacheSrc = cacheSrc;
  }

  private final AtomicLong                           idNext   = new AtomicLong(1L);
  private final ConcurrentHashMap<Long, ObjectCache> cacheMap = new ConcurrentHashMap<>();

  public <T> T cacheObject(T cachingObject) {

    var objectCache = ObjectCache.create(cachingObject, cacheSrc);
    if (objectCache == null) {
      return cachingObject;
    }

    var id = idNext.getAndIncrement();
    cacheMap.put(id, objectCache);

    //noinspection unchecked
    return (T) objectCache.cachedObject;

  }

  public void invalidateAll() {
    cacheMap.values().forEach(ObjectCache::invalidateAll);
  }

  public void initConfigs() {
    cacheMap.values().forEach(ObjectCache::initConfigs);
  }
}
