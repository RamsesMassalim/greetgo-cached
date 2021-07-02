package kz.greetgo.cached.core.util.proxy;

public interface ProxyGenerator {

  Object createProxy(Class<?> proxyInstanceClass, MethodCallHandler methodCallHandler);

}
