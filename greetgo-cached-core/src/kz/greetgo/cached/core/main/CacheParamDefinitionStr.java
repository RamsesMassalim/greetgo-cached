package kz.greetgo.cached.core.main;

public class CacheParamDefinitionStr implements CacheParamDefinition {
  private final String name;
  private final String description;
  private final String defaultValue;

  private CacheParamDefinitionStr(String name, String description, String defaultValue) {
    this.name         = name;
    this.description  = description;
    this.defaultValue = defaultValue;
  }

  public static CacheParamDefinitionStr of(String name, String description, String defaultValue) {
    return new CacheParamDefinitionStr(name, description, defaultValue);
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
    return paramStrValue;
  }

  @Override
  public String valueToStr(Object paramValue) {
    return (String) paramValue;
  }
}
