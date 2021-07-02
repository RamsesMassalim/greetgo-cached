package kz.greetgo.cached.core.main;

import kz.greetgo.cached.core.file_storage.ParamsFileStorage;
import kz.greetgo.cached.core.util.proxy.ProxyGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import static java.util.Objects.requireNonNull;

public class CacheManager {

  public static class Builder {
    private       ParamsFileStorage paramsFileStorage;
    private       ProxyGenerator    proxyGenerator;
    private       String            configFileExtension       = ".conf";
    private       String            configErrorsFileExtension = ".conf-errors";
    private       long              accessParamsDelayMillis   = 5000;
    private       LongSupplier      currentTimeMillis         = System::currentTimeMillis;
    private final CacheEngines      cacheEngines              = new CacheEngines();

    public Builder useDefaultCacheEngine_caffeine() {
      return useCacheEngine_caffeine(null);
    }

    public Builder useCacheEngine_caffeine(String caffeineEngineName) {
      Class<?> caffeineClass;
      try {
        caffeineClass = Class.forName("kz.greetgo.cached.caffeine.CacheEngineCaffeine");
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("F4K5UUE5ov :: No required dependency. Please add dependency:" +
                                     " group=`kz.greetgo.cached`, artifactId=`greetgo-cached-caffeine`", e);
      }

      CacheEngine cacheEngine;

      try {
        cacheEngine = (CacheEngine) caffeineClass.getConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }

      cacheEngines.put(caffeineEngineName, cacheEngine);
      return this;
    }

    public Builder useCacheEngine(String engineName, CacheEngine cacheEngine) {
      cacheEngines.put(engineName, cacheEngine);
      return this;
    }

    public Builder useDefaultCacheEngine(CacheEngine cacheEngine) {
      cacheEngines.put(null, cacheEngine);
      return this;
    }

    public Builder paramsFileStorage(ParamsFileStorage paramsFileStorage) {
      requireNonNull(paramsFileStorage, "49ab4pBKjS :: paramsFileStorage");
      this.paramsFileStorage = paramsFileStorage;
      return this;
    }

    public Builder proxyGenerator(ProxyGenerator proxyGenerator) {
      requireNonNull(proxyGenerator, "2yD0PCsmWv :: proxyGenerator");
      this.proxyGenerator = proxyGenerator;
      return this;
    }

    public Builder proxyGenerator_useCglib() {
      String className  = "kz.greetgo.cached.proxy.cglib.ProxyGeneratorCglib";
      String artifactId = "greetgo-cached-proxy-cglib";
      return proxyGenerator_use(className, artifactId);
    }

    @SuppressWarnings("unused")
    public Builder proxyGenerator_useSpring() {
      String className  = "kz.greetgo.cached.proxy.spring.ProxyGeneratorSpring";
      String artifactId = "greetgo-cached-proxy-spring";
      return proxyGenerator_use(className, artifactId);
    }

    private Builder proxyGenerator_use(String className, String artifactId) {
      try {
        Class<?> generatorClass = Class.forName(className);
        this.proxyGenerator = (ProxyGenerator) generatorClass.getConstructor().newInstance();
        return this;
      } catch (ClassNotFoundException e) {

        throw new RuntimeException("fK3q75Zih8 :: No required dependency."
                                     + " Add dependency: group=`kz.greetgo.cached`,"
                                     + " artifactId=`" + artifactId + "`", e);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    public Builder configFileExtension(String configFileExtension) {
      requireNonNull(configFileExtension, "COI5lIQ3w8 :: configFileExtension");
      this.configFileExtension = configFileExtension;
      return this;
    }

    public Builder configErrorsFileExtension(String configErrorsFileExtension) {
      requireNonNull(configErrorsFileExtension, "ZW2177bxQx :: configErrorsFileExtension");
      this.configErrorsFileExtension = configErrorsFileExtension;
      return this;
    }

    public Builder accessParamsDelayMillis(long accessParamsDelayMillis) {
      this.accessParamsDelayMillis = accessParamsDelayMillis;
      return this;
    }

    public Builder currentTimeMillis(LongSupplier currentTimeMillis) {
      requireNonNull(currentTimeMillis, "SBzJV1NluW :: currentTimeMillis");
      this.currentTimeMillis = currentTimeMillis;
      return this;
    }

    private Builder() {}

    public CacheManager build() {

      try {
        cacheEngines.select(null);
      } catch (NoDefaultCacheEngine e) {
        throw new RuntimeException("8ZS0h91V3q :: No default cache engine."
                                     + "\n\t\tPlease call `builder.useDefaultCacheEngine...()`", e);
      }

      requireNonNull(paramsFileStorage, "JP3SLs6zUA :: paramsFileStorage");
      requireNonNull(proxyGenerator, "8Zr8tSZ4W1 :: No proxyGenerator."
        + "\n\t\tPlease call `builder.proxyGenerator_useSpring()` if you use Spring Framework,"
        + "\n\t\tor call `builder.proxyGenerator_useCglib()` for using cglib");
      requireNonNull(configFileExtension, "HFeEer3Ai8 :: configFileExtension");
      requireNonNull(configErrorsFileExtension, "p03l2sYFP4 :: configErrorsFileExtension");
      requireNonNull(currentTimeMillis, "Pm7g9EtPVy :: currentTimeMillis");

      CacheSrc cacheSrc = new CacheSrc(cacheEngines, paramsFileStorage, proxyGenerator, configFileExtension,
                                       configErrorsFileExtension, accessParamsDelayMillis, currentTimeMillis);

      return new CacheManager(cacheSrc);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final CacheSrc cacheSrc;

  private CacheManager(CacheSrc cacheSrc) {
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
