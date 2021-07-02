package kz.greetgo.cached.core.main;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CacheParamsStorage {

  /**
   * Определяет структуру параметров
   *
   * @param paramDefinitionList структура параметров
   */
  void define(List<CacheParamDefinition> paramDefinitionList);

  /**
   * Возвращает текущее значение параметров
   *
   * @return текущее значение параметров (null не бывает)
   */
  Map<String, Object> get();

  /**
   * Устанавливает или удаляет новое значение параметров
   *
   * @param params новое значение параметров
   * @return дата последней модификации
   */
  Optional<Date> set(Map<String, Object> params);

  /**
   * Возвращает дату последней модификации параметров
   *
   * @return дата последней модификации параметров, или null если параметров нет
   */
  Optional<Date> lastModifiedAt();

}
