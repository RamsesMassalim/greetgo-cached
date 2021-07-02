package kz.greetgo.cached.core.core;

public interface CacheSupplier<In, Out> {

  CoreCache<In, Out> get();

  void initConfig();

  void close();

}
