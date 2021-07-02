package kz.greetgo.cached.core.core;

import kz.greetgo.cached.core.util.DoOnce;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;

public final class CacheSupplierImpl<In, Out> implements CacheSupplier<In, Out> {

  private final CacheParamsStorage   cacheParamsStorage;
  private final CacheEngine          cacheEngine;
  private final MethodAnnotationData methodAnnotationData;
  private final long                 accessParamsDelayMillis;
  private final LongSupplier         currentTimeMillis;
  private final AtomicLong           lastGetParams = new AtomicLong(0);

  public CacheSupplierImpl(CacheParamsStorage cacheParamsStorage,
                           CacheEngine cacheEngine,
                           MethodAnnotationData methodAnnotationData,
                           long accessParamsDelayMillis,
                           LongSupplier currentTimeMillis) {
    this.cacheParamsStorage      = cacheParamsStorage;
    this.cacheEngine             = cacheEngine;
    this.methodAnnotationData    = methodAnnotationData;
    this.accessParamsDelayMillis = accessParamsDelayMillis;
    this.currentTimeMillis       = currentTimeMillis;

    lastGetParams.set(currentTimeMillis.getAsLong());
  }

  private final DoOnce cacheParamsStorageDefine = new DoOnce(t -> true);

  private final AtomicReference<Map<String, Object>> paramsRef = new AtomicReference<>(null);

  private final AtomicReference<CoreCache<In, Out>> holder = new AtomicReference<>(null);

  private final AtomicLong savedLastModifiedMillis = new AtomicLong(0);

  private CoreCache<In, Out> resetCacheWith(Map<String, Object> cacheParams) {
    CoreCache<In, Out> coreCache = cacheEngine.createCoreCache(cacheParams);
    CoreCache<In, Out> old       = holder.getAndSet(coreCache);
    if (old != null) {
      old.close();
    }
    return coreCache;
  }

  private final AtomicBoolean closed = new AtomicBoolean(false);

  @Override
  public CoreCache<In, Out> get() {
    if (closed.get()) {
      return null;
    }

    var cache = holder.get();
    if (cache == null) {
      ensureParamsStorageDefined();

      Map<String, Object> cacheParams = cacheParamsStorage.get();
      paramsRef.set(cacheParams);
      return resetCacheWith(cacheParams);
    }

    {
      var  currentTime = currentTimeMillis.getAsLong();
      long delta       = currentTime - lastGetParams.get();
      if (delta <= accessParamsDelayMillis) {
        return cache;
      }
      lastGetParams.set(currentTime);
    }

    {
      var lastModified = cacheParamsStorage.lastModifiedAt().orElse(null);
      if (lastModified == null) {
        savedLastModifiedMillis.set(0);
        return resetCacheWith(cacheParamsStorage.get());
      }
      var savedTime = savedLastModifiedMillis.get();
      if (savedTime == 0L) {
        savedLastModifiedMillis.set(lastModified.getTime());
        return cache;
      }
      var lastModifiedTime = lastModified.getTime();
      if (savedTime == lastModifiedTime) {
        return cache;
      }
      savedLastModifiedMillis.set(lastModifiedTime);
    }

    return resetCacheWith(cacheParamsStorage.get());
  }

  @Override
  public void initConfig() {
    ensureParamsStorageDefined();
    cacheParamsStorage.get();
  }

  private void ensureParamsStorageDefined() {
    cacheParamsStorageDefine.doOnce(() -> cacheParamsStorage.define(cacheEngine.paramList(methodAnnotationData)));
  }

  @Override
  public void close() {
    closed.set(true);
    var cache = holder.getAndSet(null);
    if (cache != null) {
      cache.close();
    }
  }
}
