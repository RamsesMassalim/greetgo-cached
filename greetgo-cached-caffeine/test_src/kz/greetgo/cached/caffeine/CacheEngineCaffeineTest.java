package kz.greetgo.cached.caffeine;

import kz.greetgo.cached.core.main.CacheManager;
import kz.greetgo.cached.core.main.CoreCache;
import org.testng.annotations.Test;

import java.util.Map;

import static kz.greetgo.cached.caffeine.CacheEngineCaffeine.LIFE_TIME_MILLIS;
import static kz.greetgo.cached.caffeine.CacheEngineCaffeine.MAXIMUM_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

public class CacheEngineCaffeineTest {

  @Test
  public void cacheManager() {

    CacheManager.builder()
                .useDefaultCacheEngine_caffeine();

  }

  @Test
  public void checkCacheWorking() {
    var cacheEngineCaffeine = new CacheEngineCaffeine();

    var coreCache = cacheEngineCaffeine.createCoreCache(Map.of(LIFE_TIME_MILLIS, 3000L, MAXIMUM_SIZE, 10L));

    var asd1 = coreCache.get("asd", () -> "asd before");
    var asd2 = coreCache.get("asd", () -> "asd after");

    assertThat(asd1).isEqualTo("asd before");
    assertThat(asd2).isEqualTo("asd before");
  }
}
