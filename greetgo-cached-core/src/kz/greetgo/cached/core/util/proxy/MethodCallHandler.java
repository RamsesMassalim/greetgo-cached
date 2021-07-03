package kz.greetgo.cached.core.util.proxy;

import java.lang.reflect.Method;

public interface MethodCallHandler {

  Object handle(Object proxyObject,
                Method method, Object[] args,
                MethodProxyInvoker methodProxyInvoker) throws Throwable;

}
