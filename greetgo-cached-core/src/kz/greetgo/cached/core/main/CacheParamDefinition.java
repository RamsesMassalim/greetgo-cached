package kz.greetgo.cached.core.main;

public interface CacheParamDefinition {

  String name();

  String description();

  Object defaultValue();

  Object strToValue(String paramStrValue);

  String valueToStr(Object paramValue);

}
