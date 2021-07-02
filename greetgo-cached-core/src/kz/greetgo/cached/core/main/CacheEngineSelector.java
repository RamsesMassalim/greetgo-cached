package kz.greetgo.cached.core.main;

/**
 * Выбирает движок кэширования
 */
public interface CacheEngineSelector {

  /**
   * Выбирает движок кэширования
   *
   * @param cacheEngineName имя движка кэширования, null - запросить движок кэширования по умолчанию
   * @return движок кэширования
   * @throws NoCacheEngineWithName генерируется, если указанного движка кэширования не найдено
   */
  CacheEngine select(String cacheEngineName) throws NoCacheEngineWithName;

}
