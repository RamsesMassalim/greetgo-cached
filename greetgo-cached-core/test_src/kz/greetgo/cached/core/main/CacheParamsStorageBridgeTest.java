package kz.greetgo.cached.core.main;

import kz.greetgo.cached.core.Cached;
import kz.greetgo.cached.core.annotations.CacheDescription;
import kz.greetgo.cached.core.test_util.TestParamsFileStorage;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheParamsStorageBridgeTest {

  public static class TestController {

    @CacheDescription("Комментарий метода\nв три строки\nтретья строка")
    public Cached<String> methodOne(String input) {
      return () -> Optional.of("ret " + input);
    }

  }

  @Test
  public void readDefaultParams() throws Exception {

    var fs = new TestParamsFileStorage(Date::new);

    Class<?> controllerClass = TestController.class;

    var methodOne = controllerClass.getMethod("methodOne", String.class);

    var cps = CacheParamsStorageBridge.Builder.on(fs)
                                              .controllerMethod(controllerClass, methodOne)
                                              .fileExtension(".test-conf")
                                              .errorFileExtension(".test-conf-errors")
                                              .build();
    List<CacheParamDefinition> cpdList = new ArrayList<>();
    cpdList.add(CacheParamDefinitionInt.of("count", "Количество баранов\nу входа в магазин", 37));
    cpdList.add(CacheParamDefinitionLong.of("value", "Величина тока\nнад дверью", 370L));
    cpdList.add(CacheParamDefinitionLong.of("value2", null, 178L));
    cpdList.add(CacheParamDefinitionStr.of("url", "Доступ", "http://access"));
    cps.define(cpdList);

    var readCacheParams = cps.get();

    System.out.println("3u6jVsYnE9 :: keys = " + fs.pathContentMap.keySet());

    String k = "TestController.test-conf";

    assertThat(fs.pathContentMap).containsKey(k);
    TestParamsFileStorage.Content content = fs.pathContentMap.get(k);
    System.out.println("81hbu8nMEX :: content = " + content.text);
    var lines = Arrays.stream(content.text.split("\n")).collect(Collectors.toSet());

    assertThat(lines).contains("methodOne__count=37");
    assertThat(lines).contains("methodOne__value=370");
    assertThat(lines).contains("methodOne__value2=178");
    assertThat(lines).contains("methodOne__url=http://access");

    System.out.println("SsWL18ctJu :: readCacheParams = " + readCacheParams);

    assertThat(readCacheParams).isNotNull();
    assertThat(readCacheParams.get("count")).isEqualTo(37);
    assertThat(readCacheParams.get("value")).isEqualTo(370L);
    assertThat(readCacheParams.get("value2")).isEqualTo(178L);
    assertThat(readCacheParams.get("url")).isEqualTo("http://access");

  }

  @Test
  public void readFromConfig() throws Exception {

    var fs = new TestParamsFileStorage(Date::new);

    fs.write("TestController.test-conf", "\n" +
      "  methodOne__count   = 7766     \n" +
      "methodOne__value=34214\n" +
      "methodOne__value2=66546\n" +
      "methodOne__url=http://access.kz/123789/432154/333\n");

    fs.doErrorOnWrite.add("TestController.test-conf");

    Class<?> controllerClass = TestController.class;

    var methodOne = controllerClass.getMethod("methodOne", String.class);

    var cps = CacheParamsStorageBridge.Builder.on(fs)
                                              .controllerMethod(controllerClass, methodOne)
                                              .fileExtension(".test-conf")
                                              .errorFileExtension(".test-conf-errors")
                                              .build();
    List<CacheParamDefinition> cpdList = new ArrayList<>();
    cpdList.add(CacheParamDefinitionInt.of("count", "Количество баранов\nу входа в магазин", 37));
    cpdList.add(CacheParamDefinitionLong.of("value", "Величина тока\nнад дверью", 370L));
    cpdList.add(CacheParamDefinitionLong.of("value2", null, 178L));
    cpdList.add(CacheParamDefinitionStr.of("url", "Доступ", "http://access"));
    cps.define(cpdList);

    var readCacheParams = cps.get();

    System.out.println("3u6jVsYnE9 :: keys = " + fs.pathContentMap.keySet());

    String k = "TestController.test-conf";

    assertThat(fs.pathContentMap).containsKey(k);
    TestParamsFileStorage.Content content = fs.pathContentMap.get(k);
    System.out.println("hT13w5031y :: content = " + content.text);
    var lines = Arrays.stream(content.text.split("\n")).collect(Collectors.toSet());

    assertThat(lines).contains("  methodOne__count   = 7766     ");
    assertThat(lines).contains("methodOne__value=34214");
    assertThat(lines).contains("methodOne__value2=66546");
    assertThat(lines).contains("methodOne__url=http://access.kz/123789/432154/333");


    System.out.println("f86dHuBeT4 :: readCacheParams = " + readCacheParams);

    assertThat(readCacheParams).isNotNull();
    assertThat(readCacheParams.get("count")).isEqualTo(7766);
    assertThat(readCacheParams.get("value")).isEqualTo(34214L);
    assertThat(readCacheParams.get("value2")).isEqualTo(66546L);
    assertThat(readCacheParams.get("url")).isEqualTo("http://access.kz/123789/432154/333");

  }

  @Test
  public void readFromIncompleteConfig_andWriteToComplete() throws Exception {

    var fs = new TestParamsFileStorage(Date::new);

    fs.write("TestController.test-conf", "\n" +
      "methodOne__value=3884765\n" +
      "methodOne__url=http://access.kz/543278/67543/6532\n");

    Class<?> controllerClass = TestController.class;

    var methodOne = controllerClass.getMethod("methodOne", String.class);

    var cps = CacheParamsStorageBridge.Builder.on(fs)
                                              .controllerMethod(controllerClass, methodOne)
                                              .fileExtension(".test-conf")
                                              .errorFileExtension(".test-conf-errors")
                                              .build();
    List<CacheParamDefinition> cpdList = new ArrayList<>();
    cpdList.add(CacheParamDefinitionInt.of("count", "Количество баранов\nу входа в магазин", 37));
    cpdList.add(CacheParamDefinitionLong.of("value", "Величина тока\nнад дверью", 370L));
    cpdList.add(CacheParamDefinitionLong.of("value2", null, 178L));
    cpdList.add(CacheParamDefinitionStr.of("url", "Доступ", "http://access"));
    cps.define(cpdList);

    var readCacheParams = cps.get();

    System.out.println("mGLZSl0IrO :: keys = " + fs.pathContentMap.keySet());

    String k = "TestController.test-conf";

    assertThat(fs.pathContentMap).containsKey(k);
    TestParamsFileStorage.Content content = fs.pathContentMap.get(k);
    System.out.println("pa2xTX77XB :: content = " + content.text);
    var lines = Arrays.stream(content.text.split("\n")).collect(Collectors.toSet());

    assertThat(lines).contains("methodOne__count=37");
    assertThat(lines).contains("methodOne__value=3884765");
    assertThat(lines).contains("methodOne__value2=178");
    assertThat(lines).contains("methodOne__url=http://access.kz/543278/67543/6532");


    System.out.println("88KJ8x9T0B :: readCacheParams = " + readCacheParams);

    assertThat(readCacheParams).isNotNull();
    assertThat(readCacheParams.get("count")).isEqualTo(37);
    assertThat(readCacheParams.get("value")).isEqualTo(3884765L);
    assertThat(readCacheParams.get("value2")).isEqualTo(178L);
    assertThat(readCacheParams.get("url")).isEqualTo("http://access.kz/543278/67543/6532");

  }

  @Test
  public void readFromConfigWithErrors__allParametersExists() throws Exception {

    var fs = new TestParamsFileStorage(Date::new);

    fs.write("TestController.test-conf", "\n#dd\n#wow\n" +
      "methodOne__count=77wow66\n" +
      "methodOne__value=3asd42dsa14\n" +
      "methodOne__value2=66546\n" +
      "methodOne__url=http://access.kz/123789/432154/333\n");

    fs.doErrorOnWrite.add("TestController.test-conf");

    Class<?> controllerClass = TestController.class;

    var methodOne = controllerClass.getMethod("methodOne", String.class);

    var cps = CacheParamsStorageBridge.Builder.on(fs)
                                              .controllerMethod(controllerClass, methodOne)
                                              .fileExtension(".test-conf")
                                              .errorFileExtension(".test-conf-errors")
                                              .build();
    List<CacheParamDefinition> cpdList = new ArrayList<>();
    cpdList.add(CacheParamDefinitionInt.of("count", "Количество баранов\nу входа в магазин", 37));
    cpdList.add(CacheParamDefinitionLong.of("value", "Величина тока\nнад дверью", 370L));
    cpdList.add(CacheParamDefinitionLong.of("value2", null, 178L));
    cpdList.add(CacheParamDefinitionStr.of("url", "Доступ", "http://access"));
    cps.define(cpdList);

    var readCacheParams = cps.get();

    System.out.println("VOdfZp9ts1 :: keys = " + fs.pathContentMap.keySet());

    String k = "TestController.test-conf";

    assertThat(fs.pathContentMap).containsKey(k);
    TestParamsFileStorage.Content content = fs.pathContentMap.get(k);
    System.out.println("Z8v5oBE7pb :: content = " + content.text);
    var lines = Arrays.stream(content.text.split("\n")).collect(Collectors.toSet());

    assertThat(lines).contains("methodOne__count=77wow66");
    assertThat(lines).contains("methodOne__value=3asd42dsa14");
    assertThat(lines).contains("methodOne__value2=66546");
    assertThat(lines).contains("methodOne__url=http://access.kz/123789/432154/333");


    System.out.println("OWb4nrrOsw :: readCacheParams = " + readCacheParams);

    assertThat(readCacheParams).isNotNull();
    assertThat(readCacheParams.get("count")).isEqualTo(37);
    assertThat(readCacheParams.get("value")).isEqualTo(370L);
    assertThat(readCacheParams.get("value2")).isEqualTo(66546L);
    assertThat(readCacheParams.get("url")).isEqualTo("http://access.kz/123789/432154/333");

    String err = "TestController.test-conf-errors";

    assertThat(fs.pathContentMap).containsKey(err);
    TestParamsFileStorage.Content errContent = fs.pathContentMap.get(err);
    System.out.println("Nu0CfOgyM9 :: errContent = " + errContent.text);

    assertThat(errContent.text).contains("ERROR line 4 : NumberFormatException : For input string: \"77wow66\"");
    assertThat(errContent.text).contains("ERROR line 5 : NumberFormatException : For input string: \"3asd42dsa14\"");

    fs.doErrorOnWrite.remove("TestController.test-conf");

    fs.write("TestController.test-conf", "\n#dd\n#wow\n" +
      "methodOne__count=1111\n" +
      "methodOne__value=2222\n" +
      "methodOne__value2=66546\n" +
      "methodOne__url=http://access.kz/123789/432154/333\n");

    fs.doErrorOnWrite.add("TestController.test-conf");

    var readCacheParams2 = cps.get();
    assertThat(readCacheParams2).isNotNull();
    assertThat(readCacheParams2.get("count")).isEqualTo(1111);
    assertThat(readCacheParams2.get("value")).isEqualTo(2222L);
    assertThat(readCacheParams2.get("value2")).isEqualTo(66546L);
    assertThat(readCacheParams2.get("url")).isEqualTo("http://access.kz/123789/432154/333");

    assertThat(fs.pathContentMap).doesNotContainKey(err);
  }


}
