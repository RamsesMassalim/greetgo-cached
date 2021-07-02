package kz.greetgo.cached.core.main;

import kz.greetgo.cached.core.file_storage.ParamsFileStorage;
import kz.greetgo.cached.core.util.proxy.ProxyGenerator;

import java.util.function.LongSupplier;

import static java.util.Objects.requireNonNull;

public class CacheSrc {
  public final CacheEngineSelector cacheEngineSelector;
  public final ParamsFileStorage   paramsFileStorage;
  public final ProxyGenerator      proxyGenerator;
  public final String              configFileExtension;
  public final String              configErrorsFileExtension;
  public final long                accessParamsDelayMillis;
  public final LongSupplier        currentTimeMillis;

  public CacheSrc(CacheEngineSelector cacheEngineSelector,
                  ParamsFileStorage paramsFileStorage,
                  ProxyGenerator proxyGenerator,
                  String configFileExtension, String configErrorsFileExtension,
                  long accessParamsDelayMillis,
                  LongSupplier currentTimeMillis) {
    this.cacheEngineSelector       = requireNonNull(cacheEngineSelector, "cacheEngineSelector");
    this.paramsFileStorage         = requireNonNull(paramsFileStorage, "paramsFileStorage");
    this.proxyGenerator            = requireNonNull(proxyGenerator, "proxyGenerator");
    this.configFileExtension       = requireNonNull(configFileExtension, "configFileExtension");
    this.configErrorsFileExtension = requireNonNull(configErrorsFileExtension, "configErrorsFileExtension");
    this.accessParamsDelayMillis   = accessParamsDelayMillis;
    this.currentTimeMillis         = requireNonNull(currentTimeMillis, "currentTimeMillis");
  }
}
