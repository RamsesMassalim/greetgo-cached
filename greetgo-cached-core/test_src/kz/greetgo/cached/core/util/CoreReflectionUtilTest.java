package kz.greetgo.cached.core.util;

import kz.greetgo.cached.core.annotations.CacheEngineName;
import kz.greetgo.cached.core.annotations.CacheLifeTimeMillis;
import kz.greetgo.cached.core.annotations.CacheMaximumSize;
import kz.greetgo.cached.core.annotations.CacheParamInt;
import kz.greetgo.cached.core.annotations.CacheParamLong;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class CoreReflectionUtilTest {

  public static class TestClass {

    @CacheEngineName("Hello World Engine")
    @CacheMaximumSize(321_009L)
    @CacheLifeTimeMillis(123_876L)
    @CacheParamInt(name = "param_int_1", value = 76)
    @CacheParamInt(name = "param_int_2", value = 71)
    @CacheParamLong(name = "param_long_1", value = 76000000L)
    public void asd() {}

    public void nakedMethod() {}
  }

  @Test
  public void extractMethodAnnotationData() throws Exception {
    var method = TestClass.class.getMethod("asd");

    //
    //
    var annotationData = CoreReflectionUtil.extractMethodAnnotationData(method);
    //
    //

    assertThat(annotationData).isNotNull();
    assertThat(annotationData.cacheEngineName).isEqualTo("Hello World Engine");
    assertThat(annotationData.maximumSize).isEqualTo(321_009L);
    assertThat(annotationData.lifeTimeMillis).isEqualTo(123_876L);
    assertThat(annotationData.params).contains(entry("param_int_1", 76));
    assertThat(annotationData.params).contains(entry("param_int_2", 71));
    assertThat(annotationData.params).contains(entry("param_long_1", 76000000L));

  }

  @Test
  public void extractMethodAnnotationData__nakedMethod() throws Exception {
    var method = TestClass.class.getMethod("nakedMethod");

    //
    //
    var annotationData = CoreReflectionUtil.extractMethodAnnotationData(method);
    //
    //

    assertThat(annotationData).isNotNull();
    assertThat(annotationData.cacheEngineName).isNull();
    assertThat(annotationData.maximumSize).isNull();
    assertThat(annotationData.lifeTimeMillis).isNull();
    assertThat(annotationData.params).isEmpty();

  }
}
