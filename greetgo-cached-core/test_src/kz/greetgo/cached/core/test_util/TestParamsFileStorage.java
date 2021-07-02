package kz.greetgo.cached.core.test_util;

import kz.greetgo.cached.core.ParamsFileStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class TestParamsFileStorage implements ParamsFileStorage {

  private final Supplier<Date>    nowSupplier;
  public final  Set<String> doErrorOnWrite = new HashSet<>();

  @RequiredArgsConstructor
  public static class Content {
    public final String text;
    public final Date   lastModifiedAt;
  }

  public final Map<String, Content> pathContentMap = new HashMap<>();

  @Override
  public Optional<String> read(@NonNull String path) {
    return Optional.ofNullable(pathContentMap.get(path)).map(x -> x.text);
  }

  @Override
  public Optional<Date> lastModifiedAt(@NonNull String path) {
    return Optional.ofNullable(pathContentMap.get(path)).map(x -> x.lastModifiedAt);
  }

  @Override
  public Optional<Date> write(@NonNull String path, String content) {
    if (doErrorOnWrite.contains(path)) {
      throw new RuntimeException("e960Z954z9 :: write denied: path=`" + path + "`");
    }
    if (content == null) {
      pathContentMap.remove(path);
      return Optional.empty();
    }

    {
      Content xx = new Content(content, nowSupplier.get());
      pathContentMap.put(path, xx);
      return Optional.of(xx.lastModifiedAt);
    }
  }

}
