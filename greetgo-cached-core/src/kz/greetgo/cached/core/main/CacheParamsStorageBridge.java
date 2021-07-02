package kz.greetgo.cached.core.main;

import kz.greetgo.cached.core.file_storage.ParamsFileStorage;
import kz.greetgo.cached.core.annotations.CacheDescription;
import kz.greetgo.cached.core.util.ErrUtil;
import kz.greetgo.cached.core.util.ValueOrError;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.stream.Collectors.toMap;
import static kz.greetgo.cached.core.util.ReadUtil.toLineList;

public class CacheParamsStorageBridge implements CacheParamsStorage {

  public static class Builder {
    private ParamsFileStorage fileStorage;
    private String            fileExtension;
    private String            errorFileExtension;
    private Class<?>          controllerClass;
    private Method            method;

    private Builder() {}

    public static Builder on(ParamsFileStorage fileStorage) {
      var builder = new Builder();
      builder.fileStorage = fileStorage;
      return builder;
    }

    public Builder controllerMethod(Class<?> controllerClass, Method method) {
      this.controllerClass = controllerClass;
      this.method          = method;
      return this;
    }

    public Builder fileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
      return this;
    }

    public Builder errorFileExtension(String errorFileExtension) {
      this.errorFileExtension = errorFileExtension;
      return this;
    }

    public CacheParamsStorageBridge build() {
      return new CacheParamsStorageBridge(fileStorage, fileExtension, errorFileExtension,
                                          controllerClass, method);
    }
  }

  private final ParamsFileStorage fileStorage;
  private final String            fileExtension;
  private final String            errorFileExtension;
  private final Class<?>          controllerClass;
  private final Method            method;

  public CacheParamsStorageBridge(ParamsFileStorage fileStorage,
                                  String fileExtension, String errorFileExtension,
                                  Class<?> controllerClass, Method method) {
    this.fileStorage        = requireNonNull(fileStorage, "360eEe7j8C :: fileStorage");
    this.fileExtension      = requireNonNullElse(fileExtension, ".conf");
    this.errorFileExtension = requireNonNullElseGet(errorFileExtension, () -> fileExtension + ".errors");
    this.controllerClass    = requireNonNull(controllerClass, "yjO6Cbg96f :: controllerClass");
    this.method             = requireNonNull(method, "q1G40Ba90l :: method");
  }

  @RequiredArgsConstructor
  private static class Definition {
    public final List<CacheParamDefinition>        list;
    public final Map<String, CacheParamDefinition> map;
  }

  private final AtomicReference<Definition> definition = new AtomicReference<>(null);

  @Override
  public void define(List<CacheParamDefinition> paramDefinitionList) {
    requireNonNull(paramDefinitionList, "paramDefinitionList");
    definition.set(new Definition(paramDefinitionList,
                                  paramDefinitionList.stream()
                                                     .collect(toMap(CacheParamDefinition::name, x -> x))));
  }

  private String fileName() {
    return controllerClass.getSimpleName() + fileExtension;
  }

  private String errFileName() {
    return controllerClass.getSimpleName() + errorFileExtension;
  }

  @Override
  public Optional<Date> lastModifiedAt() {
    return fileStorage.lastModifiedAt(fileName());
  }

  @RequiredArgsConstructor
  private static class GetOrSetReturn {
    final Map<String, Object> cacheParams;
    final Optional<Date>      lastModifiedAt;
  }

  @Override
  public Map<String, Object> get() {
    return getOrSet(null).cacheParams;
  }

  @Override
  public Optional<Date> set(Map<String, Object> params) {
    return getOrSet(params).lastModifiedAt;
  }

  // The magic method. Only true magicians can write methods like this one.
  private GetOrSetReturn getOrSet(Map<String, Object> inputParams) {
    var def = definition.get();
    requireNonNull(def, "u9ub71AknT :: definition");

    String prefix = method.getName() + "__";

    StringBuilder errors = new StringBuilder();

    Optional<Date> lastModifiedAt = fileStorage.lastModifiedAt(fileName());
    String         content        = fileStorage.read(fileName()).orElse("");
    StringBuilder  newContent     = new StringBuilder();

    Map<String, Object> ret = new HashMap<>();

    int lineNo = 0;

    boolean update = false;

    for (final String line : content.split("\n")) {
      lineNo++;
      String trimmedLine = line.trim();
      if (trimmedLine.isEmpty()) {
        newContent.append(line).append("\n");
        continue;
      }
      if (trimmedLine.startsWith("#")) {
        newContent.append(line).append("\n");
        continue;
      }
      int idx = line.indexOf('=');

      String lineName, strValue;
      if (idx < 0) {
        lineName = trimmedLine;
        strValue = null;
      } else {
        lineName = line.substring(0, idx).trim();
        strValue = line.substring(idx + 1);
      }

      if (!lineName.startsWith(prefix)) {
        newContent.append(line).append("\n");
        continue;
      }

      String name = lineName.substring(prefix.length());

      var paramDef = def.map.get(name);
      if (paramDef == null) {
        newContent.append(line).append("\n");
        continue;
      }

      var readValueOrError = ValueOrError.of(() -> paramDef.strToValue(strValue));

      if (inputParams != null) {
        Object value = inputParams.get(name);
        if (value == null) {
          value = paramDef.defaultValue();
        }

        if (readValueOrError.isValue() && Objects.equals(readValueOrError.value(), value)) {
          newContent.append(line).append("\n");
          continue;
        }

        newContent.append(prefix).append(name).append("=").append(paramDef.valueToStr(value)).append("\n");
        update = true;
        continue;
      }

      if (readValueOrError.isValue()) {
        ret.put(name, readValueOrError.value());
      } else {
        var err = readValueOrError.error();

        ret.put(name, paramDef.defaultValue());

        errors.append("ERROR line ").append(lineNo).append(" : ").append(err.getClass().getSimpleName())
              .append(" : ").append(err.getMessage()).append("\n");

        var stackTrace = ErrUtil.extractStackTrace(err);
        errors.append("\n").append(stackTrace).append("\n");
      }

      newContent.append(line).append("\n");
    }

    List<String> toWrite = new ArrayList<>();

    for (final CacheParamDefinition pd : def.list) {
      if (ret.containsKey(pd.name())) {
        continue;
      }

      toWrite.add(pd.name());
    }

    if (toWrite.size() > 0) {
      update = true;

      var nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

      newContent.append("\n");
      newContent.append("#\n");
      newContent.append("# Class       : ").append(controllerClass.getSimpleName()).append("\n");
      newContent.append("# Method      : ").append(method.getName()).append("\n");
      newContent.append("# Appended at : ").append(nowStr).append("\n");
      newContent.append("#\n");
      var cacheDescription = method.getAnnotation(CacheDescription.class);
      if (cacheDescription != null) {
        for (final String line : toLineList(cacheDescription.value())) {
          newContent.append("# ").append(line).append("\n");
        }
      }
      newContent.append("#\n");

      for (final String paramName : toWrite) {

        var pd = def.map.get(paramName);
        newContent.append("\n");
        for (final String line : toLineList(pd.description())) {
          newContent.append("# ").append(line).append("\n");
        }

        Object value = null;

        if (inputParams != null) {
          value = inputParams.get(paramName);
        }
        if (value == null) {
          value = pd.defaultValue();
        }

        ret.put(paramName, value);
        var strValue = pd.valueToStr(value);
        if (strValue == null) {
          newContent.append(prefix).append(paramName).append("\n");
        } else {
          newContent.append(prefix).append(paramName).append("=").append(strValue).append("\n");
        }

      }
    }

    if (update) {
      lastModifiedAt = fileStorage.write(fileName(), newContent.toString());
    }

    if (errors.length() == 0) {
      fileStorage.lastModifiedAt(errFileName())
                 .ifPresent(ignore -> fileStorage.write(errFileName(), null));
    } else {
      fileStorage.write(errFileName(), errors.toString());
    }

    return new GetOrSetReturn(Map.copyOf(ret), lastModifiedAt);
  }

}
