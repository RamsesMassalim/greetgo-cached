package kz.greetgo.cached.core.core;

public class CacheParamDefinitionLong implements CacheParamDefinition {
  private final String name;
  private final String description;
  private final long   defaultValue;

  private CacheParamDefinitionLong(String name, String description, long defaultValue) {
    this.name         = name;
    this.description  = description;
    this.defaultValue = defaultValue;
  }

  public static CacheParamDefinitionLong of(String name, String description, long defaultValue) {
    return new CacheParamDefinitionLong(name, description, defaultValue);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Object defaultValue() {
    return defaultValue;
  }

  @Override
  public Object strToValue(String paramStrValue) {
    return paramStrValue == null ? 0 : Long.parseLong(paramStrValue.trim());
  }

  @Override
  public String valueToStr(Object paramValue) {
    return paramValue == null ? null : "" + paramValue;
  }
}
