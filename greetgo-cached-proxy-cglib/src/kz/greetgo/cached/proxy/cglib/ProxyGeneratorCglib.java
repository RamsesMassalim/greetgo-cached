package kz.greetgo.cached.proxy.cglib;

import kz.greetgo.cached.core.util.proxy.MethodCallHandler;
import kz.greetgo.cached.core.util.proxy.ProxyGenerator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyGeneratorCglib implements ProxyGenerator {
  @Override
  public Object createProxy(Class<?> proxyInstanceClass, MethodCallHandler methodCallHandler) {

    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(proxyInstanceClass);

    //noinspection Convert2Lambda
    enhancer.setCallback(new MethodInterceptor() {
      @Override
      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return methodCallHandler.handle(obj, method, args, proxy::invokeSuper);
      }
    });

    return enhancer.create();
  }
}
