package kz.greetgo.cached.core.test_util;


import kz.greetgo.cached.core.main.CacheEngine;
import kz.greetgo.cached.core.main.CacheParamDefinition;
import kz.greetgo.cached.core.main.CacheParamDefinitionInt;
import kz.greetgo.cached.core.main.CacheParamDefinitionLong;
import kz.greetgo.cached.core.main.CacheParamDefinitionStr;
import kz.greetgo.cached.core.main.CoreCache;
import kz.greetgo.cached.core.main.MethodAnnotationData;
import kz.greetgo.cached.core.util.ReadUtil;
import kz.greetgo.util.fui.handler.ButtonClickHandlerList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class TestCacheEngine implements CacheEngine {

  private static final String BEE_COUNT = "beeCount";
  private static final String VALUE     = "value";
  private static final String VALUE_2   = "value2";
  private static final String URL       = "url";

  @Override
  public List<CacheParamDefinition> paramList(MethodAnnotationData methodAnnotationData) {
    return List.of(
      CacheParamDefinitionInt.of(BEE_COUNT, "Количество пчёл на квадратный метр", 37),
      CacheParamDefinitionLong.of(VALUE, "Величина падения\nуральских гор\nнад континентом в прошлом", 374376847L),
      CacheParamDefinitionLong.of(VALUE_2, null, 11L),
      CacheParamDefinitionStr.of(URL, "Доступ к миру", "https://access-to-world.com")
    );
  }

  private final ButtonClickHandlerList invalidateAllHandlers = new ButtonClickHandlerList();

  public void invalidateAll() {
    invalidateAllHandlers.fire();
  }

  @Override
  public <In, Out> CoreCache<In, Out> createCoreCache(Map<String, Object> cacheParams) {
    ReadUtil.readInt(BEE_COUNT, cacheParams);
    ReadUtil.readLong(VALUE, cacheParams);
    ReadUtil.readLong(VALUE_2, cacheParams);

    class Held {
      final Out element;

      Held(Out element) {
        this.element = element;
      }
    }

    final ConcurrentHashMap<In, Held> cache = new ConcurrentHashMap<>();

    invalidateAllHandlers.attach(cache::clear);

    return new CoreCache<>() {
      @Override
      public Out get(In in, Supplier<Out> direct) {
        var held = cache.get(in);
        if (held == null) {
          held = new Held(direct.get());
          cache.put(in, held);
        }
        return held.element;
      }

      @Override
      public Map<String, Object> params() {
        return cacheParams;
      }

      @Override
      public void invalidateAll() {
        cache.clear();
      }

      @Override
      public void invalidateOn(In in) {
        cache.remove(in);
      }

      @Override
      public void close() {
        invalidateAll();
      }
    };
  }
}
