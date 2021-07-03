package kz.greetgo.cached.core.main;

import kz.greetgo.cached.core.Cached;
import kz.greetgo.cached.core.util.CoreReflectionUtil;
import kz.greetgo.cached.core.util.proxy.MethodProxyInvoker;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ObjectCache {
  public final  Object       cachedObject;
  private final Object       cachingObject;
  private final List<Method> cachedMethods;
  private final CacheSrc     cacheSrc;

  public static ObjectCache create(Object cachingObject, CacheSrc cacheSrc) {
    Class<?> cachingClass = cachingObject.getClass();

    var cachedMethods = Arrays.stream(cachingClass.getMethods())
                              .filter(method -> Cached.class.equals(method.getReturnType()))
                              .collect(Collectors.toList());

    if (cachedMethods.isEmpty()) {
      return null;
    }

    return new ObjectCache(cachingObject, cachedMethods, cacheSrc);
  }

  private ObjectCache(Object cachingObject, List<Method> cachedMethods, CacheSrc cacheSrc) {
    this.cachingObject = cachingObject;
    this.cachedMethods = cachedMethods;
    this.cacheSrc      = cacheSrc;

    for (final Method cachedMethod : cachedMethods) {
      if (cachedMethod.getParameterCount() != 1) {
        throw new RuntimeException("FiDkh8iqr5 :: Caching method can have only one argument" +
                                     " - no less, and no more. Method " + cachedMethod);
      }
    }

    var cachingClass = cachingObject.getClass();

    var allMethodsByName = Arrays.stream(cachingClass.getMethods()).collect(groupingBy(Method::getName));

    for (final Map.Entry<String, List<Method>> e : cachedMethods.stream()
                                                                .collect(groupingBy(Method::getName))
                                                                .entrySet()) {

      if (allMethodsByName.get(e.getKey()).size() > 1) {
        throw new RuntimeException("5TQ8AW5D3Y :: Caching method `" + e.getKey() + "`" +
                                     " in class `" + cachingClass + "`\n\t\t meets multiple times." +
                                     "\n\t\tCaching methods cannot be overloaded.\n\t\tPlease rename method");
      }

    }

    cachedObject = cacheSrc.proxyGenerator.createProxy(cachingClass, this::intercept);
  }

  private final ConcurrentHashMap<String, CacheSupplier<Object, Object>> cacheSupplierMap = new ConcurrentHashMap<>();

  private CacheSupplier<Object, Object> getCacheSupplier(Method method) {

    var methodName = method.getName();

    {
      var cacheSupplier = cacheSupplierMap.get(methodName);
      if (cacheSupplier != null) {
        return cacheSupplier;
      }
    }

    synchronized (cacheSupplierMap) {
      {
        var cacheSupplier = cacheSupplierMap.get(methodName);
        if (cacheSupplier != null) {
          return cacheSupplier;
        }
      }
      {
        var methodAnnotationData = CoreReflectionUtil.extractMethodAnnotationData(method);
        var cacheEngine          = cacheSrc.cacheEngineSelector.select(methodAnnotationData.cacheEngineName);

        var cacheParamsStorage = new CacheParamsStorageBridge(cacheSrc.paramsFileStorage,
                                                              cacheSrc.configFileExtension,
                                                              cacheSrc.configErrorsFileExtension,
                                                              cachingObject.getClass(), method);

        CacheSupplier<Object, Object> cacheSupplier = new CacheSupplierImpl<>
          (cacheParamsStorage, cacheEngine, methodAnnotationData, cacheSrc.accessParamsDelayMillis,
           cacheSrc.currentTimeMillis);

        cacheSupplierMap.put(methodName, cacheSupplier);

        return cacheSupplier;
      }
    }
  }

  public Object intercept(Object proxyObject, Method method, Object[] args,
                          MethodProxyInvoker methodProxyInvoker) throws Throwable {

    if (args.length != 1) {
      return methodProxyInvoker.invokeSuper(proxyObject, args);
    }

    if (!Cached.class.equals(method.getReturnType())) {
      return methodProxyInvoker.invokeSuper(proxyObject, args);
    }

    //noinspection unchecked
    Cached<Object> original = (Cached<Object>) method.invoke(cachingObject, args);

    CacheSupplier<Object, Object> cacheSupplier = getCacheSupplier(method);
    CoreCache<Object, Object>     cache         = cacheSupplier.get();

    if (cache == null) {
      return original;
    }

    return new Cached<>() {
      @Override
      public Optional<Object> opt() {
        return Optional.ofNullable(cache.get(args[0], () -> original.direct().orElse(null)));
      }

      @Override
      public Optional<Object> direct() {
        return original.direct();
      }

      @Override
      public void invalidateAll() {
        cache.invalidateAll();
      }

      @Override
      public void invalidateOne() {
        cache.invalidateOn(args[0]);
      }
    };
  }

  public void invalidateAll() {
    cacheSupplierMap.values().forEach(x -> x.get().invalidateAll());
  }

  public void initConfigs() {
    cachedMethods.stream()
                 .map(this::getCacheSupplier)
                 .forEach(CacheSupplier::initConfig);
  }

  public void invalidateGroup(String cacheGroup) {
    cacheSupplierMap.values()
                    .stream()
                    .filter(x -> x.groups().contains(cacheGroup))
                    .forEach(x -> x.get().invalidateAll());
  }
}
