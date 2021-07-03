package kz.greetgo.cached.core.util.proxy;

public interface MethodProxyInvoker {

  Object invokeSuper(Object proxyObject, Object[] args) throws Throwable;

}
