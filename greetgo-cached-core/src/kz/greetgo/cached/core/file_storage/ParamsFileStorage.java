package kz.greetgo.cached.core.file_storage;

import lombok.NonNull;

import java.util.Date;
import java.util.Optional;

/**
 * Обеспечивает хранение файлов-параметров, и предоставляем к ним доступ.
 */
public interface ParamsFileStorage {

  /**
   * Считывает содержимое файла по указанному пути
   *
   * @param path путь к файлу
   * @return содержимое указанного файла в виде строки, считанной в кодировке UTF-8. Если empty - то такого файла нет
   */
  Optional<String> read(@NonNull String path);

  /**
   * Считвает дату последней модификации файла
   *
   * @param path путь к файлу
   * @return дата последней модификации файла, или empty, если такого файла нет
   */
  Optional<Date> lastModifiedAt(@NonNull String path);

  /**
   * Записывает содержимое файла в кодировке UTF-8
   *
   * @param path    путь к файлу
   * @param content содержимое файла. Если передать null, то файл будет удалён
   * @return дата последней модификации, или empty, если файл был удалён
   */
  Optional<Date> write(@NonNull String path, String content);

}
