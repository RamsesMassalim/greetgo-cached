package kz.greetgo.cached.core.util;

import kz.greetgo.cached.core.annotations.CacheEngineName;
import kz.greetgo.cached.core.annotations.CacheGroup;
import kz.greetgo.cached.core.annotations.CacheLifeTimeSec;
import kz.greetgo.cached.core.annotations.CacheMaximumSize;
import kz.greetgo.cached.core.annotations.CacheParamInt;
import kz.greetgo.cached.core.annotations.CacheParamIntRepeat;
import kz.greetgo.cached.core.annotations.CacheParamLong;
import kz.greetgo.cached.core.annotations.CacheParamLongRepeat;
import kz.greetgo.cached.core.main.MethodAnnotationData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CoreReflectionUtil {

  public static MethodAnnotationData extractMethodAnnotationData(Method method) {

    CacheEngineName  cacheEngineName  = getAnnotation(method, CacheEngineName.class);
    CacheMaximumSize cacheMaximumSize = getAnnotation(method, CacheMaximumSize.class);
    CacheLifeTimeSec cacheLifeTimeSec = getAnnotation(method, CacheLifeTimeSec.class);

    CacheParamIntRepeat  cacheParamIntRepeat  = getAnnotation(method, CacheParamIntRepeat.class);
    CacheParamLongRepeat cacheParamLongRepeat = getAnnotation(method, CacheParamLongRepeat.class);

    CacheParamInt  cacheParamInt  = getAnnotation(method, CacheParamInt.class);
    CacheParamLong cacheParamLong = getAnnotation(method, CacheParamLong.class);

    CacheGroup cacheGroup = getAnnotation(method, CacheGroup.class);

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

    Set<String> cacheGroups = cacheGroup == null ? Set.of() : Set.of(cacheGroup.value());

    return new MethodAnnotationData(maximumSize, lifeTimeSec, engineName, Map.copyOf(params), cacheGroups);
  }

  private static <T extends Annotation> T getAnnotation(Method method, Class<T> annClass) {
    return method.getAnnotation(annClass);
  }

  public static Type extractFirstSubType(Type cachedType) {
    Objects.requireNonNull(cachedType, "Crg3l4kyQL :: cachedType");
    if (cachedType instanceof Class) {
      throw new IllegalArgumentException("Cannot extract cached type from Class :: " + cachedType);
    }
    if (cachedType instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) cachedType;
      return pt.getActualTypeArguments()[0];
    }
    throw new RuntimeException("0m5hgM777c :: Cannot extract cachedType from " + cachedType);
  }
}
