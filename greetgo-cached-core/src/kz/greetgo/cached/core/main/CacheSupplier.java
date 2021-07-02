package kz.greetgo.cached.core.main;

public interface CacheSupplier<In, Out> {

  CoreCache<In, Out> get();

  void initConfig();

  void close();

}
