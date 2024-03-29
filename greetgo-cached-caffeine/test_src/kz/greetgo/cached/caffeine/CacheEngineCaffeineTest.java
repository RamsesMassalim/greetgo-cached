package kz.greetgo.cached.caffeine;

import kz.greetgo.cached.core.main.CacheManager;
import org.testng.annotations.Test;

import java.util.List;
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

  @Test
  public void checkInvalidateOnWorking() {
    var cacheEngineCaffeine = new CacheEngineCaffeine();

    var coreCache = cacheEngineCaffeine.createCoreCache(Map.of(LIFE_TIME_MILLIS, 3000L, MAXIMUM_SIZE, 10L));

    var asd1 = coreCache.get("asd", () -> "asd before");

    coreCache.invalidateOn("asd");

    var asd2 = coreCache.get("asd", () -> "asd after");

    assertThat(asd1).isEqualTo("asd before");
    assertThat(asd2).isEqualTo("asd after");
  }

  @Test
  public void checkInvalidateOnWorking__Iterable() {
    var cacheEngineCaffeine = new CacheEngineCaffeine();

    var coreCache = cacheEngineCaffeine.createCoreCache(Map.of(LIFE_TIME_MILLIS, 3000L, MAXIMUM_SIZE, 10L));

    var asd1 = coreCache.get("asd", () -> "asd before");

    coreCache.invalidateOn(List.of("asd"));

    var asd2 = coreCache.get("asd", () -> "asd after");

    assertThat(asd1).isEqualTo("asd before");
    assertThat(asd2).isEqualTo("asd after");
  }
}
