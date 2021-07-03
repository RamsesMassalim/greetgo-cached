package kz.greetgo.cached.core.main;

import kz.greetgo.cached.core.Cached;
import kz.greetgo.cached.core.annotations.CacheDescription;
import kz.greetgo.cached.core.test_util.TestCacheEngine;
import kz.greetgo.cached.core.test_util.TestParamsFileStorage;
import kz.greetgo.cached.core.util.proxy.ProxyGenerator;
import kz.greetgo.cached.proxy.cglib.ProxyGeneratorCglib;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheManagerTest {

  public static class TestObject {

    public final AtomicReference<String> top = new AtomicReference<>("top");

    public final AtomicInteger helloIntCallCount = new AtomicInteger(0);

    @CacheDescription("Это приветливый тестовый метод\nон возвращает строку\nа принимает число")
    public Cached<String> helloInt(int value) {
      return () -> {
        helloIntCallCount.incrementAndGet();
        return value == 0 ? Optional.empty() : Optional.of("int " + value + " " + top.get());
      };
    }

    @SuppressWarnings("unused")
    public Cached<String> doThisOperation(long value) {
      return () -> Optional.of("value=" + value);
    }

  }

  @Test
  public void cacheObject__initialWay() {

    var testCacheEngine = new TestCacheEngine();

    var fs = new TestParamsFileStorage(Date::new);

    ProxyGenerator proxyGenerator = new ProxyGeneratorCglib();

    CacheManager cacheManager = CacheManager.builder()
                                            .useDefaultCacheEngine(testCacheEngine)
                                            .paramsFileStorage(fs)
                                            .proxyGenerator(proxyGenerator)
                                            .configFileExtension(".tst-conf")
                                            .configErrorsFileExtension(".tst-conf-errors")
                                            .accessParamsDelayMillis(100)
                                            .currentTimeMillis(System::currentTimeMillis)
                                            .build();

    TestObject testObject = new TestObject();
    testObject.top.set("one");

    //
    //
    //
    var cachedTestObject = cacheManager.cacheObject(testObject);
    //
    //
    //

    var resultOf11_1 = cachedTestObject.helloInt(11).orElse(null);
    var resultOf12_1 = cachedTestObject.helloInt(12).orElse(null);

    assertThat(resultOf11_1).isEqualTo("int 11 one");
    assertThat(resultOf12_1).isEqualTo("int 12 one");

    testObject.top.set("two");

    var resultOf11_2 = cachedTestObject.helloInt(11).orElse(null);
    var resultOf12_2 = cachedTestObject.helloInt(12).orElse(null);
    var resultOf13_2 = cachedTestObject.helloInt(13).orElse(null);

    assertThat(resultOf11_2).isEqualTo("int 11 one");
    assertThat(resultOf12_2).isEqualTo("int 12 one");
    assertThat(resultOf13_2).isEqualTo("int 13 two");

    testCacheEngine.invalidateAll();

    var resultOf11_3 = cachedTestObject.helloInt(11).orElse(null);
    var resultOf12_3 = cachedTestObject.helloInt(12).orElse(null);

    assertThat(resultOf11_3).isEqualTo("int 11 two");
    assertThat(resultOf12_3).isEqualTo("int 12 two");

    System.out.println("DBp3sZuIM5 :: resultOf11_1 = " + resultOf11_1);
    System.out.println("DBp3sZuIM5 :: resultOf12_1 = " + resultOf12_1);
    System.out.println("DBp3sZuIM5 :: resultOf11_2 = " + resultOf11_2);
    System.out.println("DBp3sZuIM5 :: resultOf12_2 = " + resultOf12_2);
    System.out.println("DBp3sZuIM5 :: resultOf11_3 = " + resultOf11_3);
    System.out.println("DBp3sZuIM5 :: resultOf12_3 = " + resultOf12_3);

    System.out.println("GELy0oTiS0 :: fs.pathContentMap.keySet() = " + fs.pathContentMap.keySet());
    var content = fs.pathContentMap.get("TestObject.tst-conf");
    assertThat(content).isNotNull();
    System.out.println("6irHhODyXe :: content.text = " + content.text);
    assertThat(content.text).contains("helloInt__beeCount=37");
    assertThat(content.text).contains("helloInt__value=374376847");
    assertThat(content.text).contains("helloInt__value2=11");
    assertThat(content.text).contains("helloInt__url=https://access-to-world.com");

  }

  @Test
  public void initConfigs() {

    var testCacheEngine = new TestCacheEngine();

    var fs = new TestParamsFileStorage(Date::new);

    CacheManager cacheManager = CacheManager.builder()
                                            .useDefaultCacheEngine(testCacheEngine)
                                            .paramsFileStorage(fs)
                                            .proxyGenerator_useCglib()
                                            .configFileExtension(".tst-conf")
                                            .configErrorsFileExtension(".tst-conf-errors")
                                            .accessParamsDelayMillis(100)
                                            .currentTimeMillis(System::currentTimeMillis)
                                            .build();

    TestObject testObject = new TestObject();
    testObject.top.set("one");

    cacheManager.cacheObject(testObject);

    {
      var content = fs.pathContentMap.get("TestObject.tst-conf");
      assertThat(content).isNull();
    }

    //
    //
    cacheManager.initConfigs();
    //
    //

    var content = fs.pathContentMap.get("TestObject.tst-conf");
    assertThat(content).isNotNull();

    System.out.println("3EL59Ch9K8 :: content.text = " + content.text);

    assertThat(content.text).contains("helloInt__beeCount=");
    assertThat(content.text).contains("helloInt__value=");
    assertThat(content.text).contains("helloInt__value2=");
    assertThat(content.text).contains("helloInt__url=");

    assertThat(content.text).contains("doThisOperation__beeCount=");
    assertThat(content.text).contains("doThisOperation__value=");
    assertThat(content.text).contains("doThisOperation__value2=");
    assertThat(content.text).contains("doThisOperation__url=");
  }

  @Test
  public void proxyGenerator_useCglib() {
    CacheManager.builder().proxyGenerator_useCglib();
  }

  @Test
  public void init() {
    Path dir = Paths.get("build/" + getClass().getSimpleName() + "/params_file_storage");

    var cacheManager = CacheManager.builder()
                                   .useDefaultCacheEngine(new TestCacheEngine())
                                   .paramsInDir(dir)
                                   .proxyGenerator_useCglib()
                                   .build();

    System.out.println("QM6G6r62W3 :: cacheManager = " + cacheManager);

    TestObject testObject = new TestObject();
    testObject.top.set("one");

    var cachedTestObject = cacheManager.cacheObject(testObject);

    System.out.println("W1HnxUjo4v :: cachedTestObject = " + cachedTestObject);

    cacheManager.initConfigs();

  }
}
