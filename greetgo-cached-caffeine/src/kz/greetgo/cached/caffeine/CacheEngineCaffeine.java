package kz.greetgo.cached.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import kz.greetgo.cached.core.main.CacheEngine;
import kz.greetgo.cached.core.main.CacheParamDefinition;
import kz.greetgo.cached.core.main.CacheParamDefinitionLong;
import kz.greetgo.cached.core.main.CoreCache;
import kz.greetgo.cached.core.main.CoreCacheEmpty;
import kz.greetgo.cached.core.main.MethodAnnotationData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static kz.greetgo.cached.core.util.ReadUtil.readLong;

public class CacheEngineCaffeine implements CacheEngine {

  static final String LIFE_TIME_MILLIS = "lifeTimeMillis";

  static final String MAXIMUM_SIZE = "maximumSize";

  @Override
  public List<CacheParamDefinition> paramList(MethodAnnotationData methodAnnotationData) {
    return List.of(
      CacheParamDefinitionLong.of(MAXIMUM_SIZE, "Максимальный размер элементов в кэше",
                                  methodAnnotationData.maximumSizeOr(1000))
      ,
      CacheParamDefinitionLong.of(LIFE_TIME_MILLIS, "Время жизни одного элемента в кэше в секундах",
                                  methodAnnotationData.lifeTimeMillisOr(1000))
    );
  }

  @Override
  public <In, Out> CoreCache<In, Out> createCoreCache(Map<String, Object> cacheParams) {

    long maximumSize = readLong(MAXIMUM_SIZE, cacheParams);

    if (maximumSize == 0) {
      return new CoreCacheEmpty<>(cacheParams);
    }

    long lifeTimeMillis = readLong(LIFE_TIME_MILLIS, cacheParams);

    Cache<In, Out> cache = Caffeine.newBuilder()
                                   .maximumSize(maximumSize)
                                   .expireAfterWrite(lifeTimeMillis, TimeUnit.MILLISECONDS)
                                   .build();

    return new CoreCache<>() {
      @Override
      public Out get(In in, Supplier<Out> direct) {
        return cache.get(in, x -> direct.get());
      }

      @Override
      public Map<String, Object> params() {
        return cacheParams;
      }

      @Override
      public void invalidateAll() {
        cache.invalidateAll();
      }

      @Override
      public void invalidateOn(In in) {
        if (in instanceof Iterable) {
          cache.invalidateAll((Iterable<?>) in);
        } else {
          cache.invalidate(in);
        }
      }

      @Override
      public void close() {
        cache.cleanUp();
      }
    };

  }
}
