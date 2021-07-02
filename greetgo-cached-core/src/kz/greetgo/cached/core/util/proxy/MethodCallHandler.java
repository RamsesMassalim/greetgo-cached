package kz.greetgo.cached.core.util.proxy;

import java.lang.reflect.Method;

public interface MethodCallHandler {

  Object handle(Method method, Object[] args) throws Throwable;

}
