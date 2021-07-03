package kz.greetgo.cached.core.main;

import java.util.Set;

public interface CacheSupplier<In, Out> {

  CoreCache<In, Out> get();

  void initConfig();

  void close();

  Set<String> groups();

}
