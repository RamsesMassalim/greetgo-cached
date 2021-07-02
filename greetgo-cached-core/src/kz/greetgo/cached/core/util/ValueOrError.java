package kz.greetgo.cached.core.util;

public interface ValueOrError<T> {
  T value();

  Throwable error();

  default boolean isValue() {
    return !isError();
  }

  boolean isError();

  interface ValueCalculator<T> {
    T calculate() throws Throwable;
  }

  static <T> ValueOrError<T> of(ValueCalculator<T> calculator) {
    try {
      var value = calculator.calculate();
      return new ValueOrError<>() {
        @Override
        public T value() {
          return value;
        }

        @Override
        public Throwable error() {
          return null;
        }

        @Override
        public boolean isError() {
          return false;
        }
      };
    } catch (Throwable error) {
      return new ValueOrError<>() {
        @Override
        public T value() {
          return null;
        }

        @Override
        public Throwable error() {
          return error;
        }

        @Override
        public boolean isError() {
          return true;
        }
      };
    }
  }
}
