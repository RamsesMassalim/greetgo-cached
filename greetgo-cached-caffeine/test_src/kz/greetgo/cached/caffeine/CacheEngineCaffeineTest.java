package kz.greetgo.cached.caffeine;

import kz.greetgo.cached.core.main.CacheManager;
import org.testng.annotations.Test;

public class CacheEngineCaffeineTest {

  @Test
  public void cacheManager() {

    CacheManager.builder()
                .useDefaultCacheEngine_caffeine();

  }

}
