package kz.greetgo.cached.core.main;

public class CacheParamDefinitionInt implements CacheParamDefinition {
  private final String name;
  private final String description;
  private final int    defaultValue;

  private CacheParamDefinitionInt(String name, String description, int defaultValue) {
    this.name         = name;
    this.description  = description;
    this.defaultValue = defaultValue;
  }

  public static CacheParamDefinitionInt of(String name, String description, int defaultValue) {
    return new CacheParamDefinitionInt(name, description, defaultValue);
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
    return paramStrValue == null ? 0 : Integer.parseInt(paramStrValue.trim());
  }

  @Override
  public String valueToStr(Object paramValue) {
    return paramValue == null ? null : "" + paramValue;
  }
}
