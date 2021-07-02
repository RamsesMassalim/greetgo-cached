package kz.greetgo.cached.core.util;

import kz.greetgo.cached.core.annotations.CacheEngineName;
import kz.greetgo.cached.core.annotations.CacheLifeTimeSec;
import kz.greetgo.cached.core.annotations.CacheMaximumSize;
import kz.greetgo.cached.core.annotations.CacheParamInt;
import kz.greetgo.cached.core.annotations.CacheParamIntRepeat;
import kz.greetgo.cached.core.annotations.CacheParamLong;
import kz.greetgo.cached.core.annotations.CacheParamLongRepeat;
import kz.greetgo.cached.core.core.MethodAnnotationData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class CoreReflectionUtil {

  public static MethodAnnotationData extractMethodAnnotationData(Method method) {

    CacheEngineName  cacheEngineName  = CoreReflectionUtil.getAnnotation(method, CacheEngineName.class);
    CacheMaximumSize cacheMaximumSize = getAnnotation(method, CacheMaximumSize.class);
    CacheLifeTimeSec cacheLifeTimeSec = getAnnotation(method, CacheLifeTimeSec.class);

    CacheParamIntRepeat  cacheParamIntRepeat  = getAnnotation(method, CacheParamIntRepeat.class);
    CacheParamLongRepeat cacheParamLongRepeat = getAnnotation(method, CacheParamLongRepeat.class);

    CacheParamInt  cacheParamInt  = getAnnotation(method, CacheParamInt.class);
    CacheParamLong cacheParamLong = getAnnotation(method, CacheParamLong.class);

    String engineName  = cacheEngineName == null ? null : cacheEngineName.value();
    Long   maximumSize = cacheMaximumSize == null ? null : cacheMaximumSize.value();
    Long   lifeTimeSec = cacheLifeTimeSec == null ? null : cacheLifeTimeSec.value();

    Map<String, Object> params = new HashMap<>();

    if (cacheParamLongRepeat != null) {
      for (var ann : cacheParamLongRepeat.value()) {
        params.put(ann.name(), ann.value());
      }
    }
    if (cacheParamIntRepeat != null) {
      for (var ann : cacheParamIntRepeat.value()) {
        params.put(ann.name(), ann.value());
      }
    }
    if (cacheParamInt != null) {
      params.put(cacheParamInt.name(), cacheParamInt.value());
    }
    if (cacheParamLong != null) {
      params.put(cacheParamLong.name(), cacheParamLong.value());
    }

    return new MethodAnnotationData(maximumSize, lifeTimeSec, engineName, Map.copyOf(params));
  }

  private static <T extends Annotation> T getAnnotation(Method method, Class<T> accClass) {
    throw new RuntimeException("02.07.2021 10:36: Not impl yet: CoreReflectionUtil.getAnnotation");
  }

}
