package kz.greetgo.cached.core;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ParamsFileStorageFs implements ParamsFileStorage {

  private final Path rootDir;

  public ParamsFileStorageFs(Path rootDir) {
    try {
      this.rootDir = rootDir.toRealPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Path realize(@NonNull String strPath) {
    while (strPath.startsWith("/")) {
      strPath = strPath.substring(1);
    }
    try {
      var ret = rootDir.resolve(strPath).toRealPath();

      if (ret.startsWith(rootDir)) {
        return ret;
      }

      throw new RuntimeException("63q7B6EV19 :: Доступ к пути `" + ret + "` закрыт." +
                                   " Разрешён доступ только внутри `" + rootDir + "`. path = `" + strPath + "`");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Optional<String> read(@NonNull String path) {
    var realPath = realize(path);
    if (!Files.exists(realPath)) {
      return Optional.empty();
    }
    try {
      var bytes = Files.readAllBytes(realPath);
      return Optional.of(new String(bytes, UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Date> lastModifiedAt(@NonNull String path) {
    var realPath = realize(path);
    if (!Files.exists(realPath)) {
      return Optional.empty();
    }
    try {
      FileTime time = Files.getLastModifiedTime(realPath);
      return Optional.of(new Date(time.toMillis()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Date> write(@NonNull String path, String content) {
    var realPath = realize(path);
    if (content == null) {
      if (Files.exists(realPath)) {
        try {
          Files.delete(realPath);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      return Optional.empty();
    }

    realPath.toFile().getParentFile().mkdirs();
    try {
      Files.write(realPath, content.getBytes(UTF_8));
      var lastModifiedTime = Files.getLastModifiedTime(realPath);
      return Optional.of(new Date(lastModifiedTime.toMillis()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
