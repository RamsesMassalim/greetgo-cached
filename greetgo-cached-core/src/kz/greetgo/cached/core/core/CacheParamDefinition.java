package kz.greetgo.cached.core.core;

public interface CacheParamDefinition {

  String name();

  String description();

  Object defaultValue();

  Object strToValue(String paramStrValue);

  String valueToStr(Object paramValue);

}
