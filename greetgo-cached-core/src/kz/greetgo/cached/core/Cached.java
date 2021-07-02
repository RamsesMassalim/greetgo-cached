package kz.greetgo.cached.core;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Cached<T> {
  default Optional<T> opt() {
    return direct();
  }

  Optional<T> direct();

  default void invalidateOne() {}

  default void invalidateAll() {}

  /**
   * If a value is present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if a value is present, otherwise {@code false}
   */
  default boolean isPresent() {
    return opt().isPresent();
  }

  /**
   * If a value is  not present, returns {@code true}, otherwise
   * {@code false}.
   *
   * @return {@code true} if a value is not present, otherwise {@code false}
   * @since 11
   */
  default boolean isEmpty() {
    return opt().isEmpty();
  }

  /**
   * If a value is present, performs the given action with the value,
   * otherwise does nothing.
   *
   * @param action the action to be performed, if a value is present
   * @throws NullPointerException if value is present and the given action is
   *                              {@code null}
   */
  default void ifPresent(Consumer<? super T> action) {
    opt().ifPresent(action);
  }

  /**
   * If a value is present, performs the given action with the value,
   * otherwise performs the given empty-based action.
   *
   * @param action      the action to be performed, if a value is present
   * @param emptyAction the empty-based action to be performed, if no value is
   *                    present
   * @throws NullPointerException if a value is present and the given action
   *                              is {@code null}, or no value is present and the given empty-based
   *                              action is {@code null}.
   * @since 9
   */
  @SuppressWarnings("unused")
  default void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
    opt().ifPresentOrElse(action, emptyAction);
  }

  /**
   * If a value is present, and the value matches the given predicate,
   * returns an {@code Optional} describing the value, otherwise returns an
   * empty {@code Optional}.
   *
   * @param predicate the predicate to apply to a value, if present
   * @return an {@code Optional} describing the value of this
   * {@code Optional}, if a value is present and the value matches the
   * given predicate, otherwise an empty {@code Optional}
   * @throws NullPointerException if the predicate is {@code null}
   */
  default Optional<T> filter(Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate);
    return opt().filter(predicate);
  }

  /**
   * If a value is present, returns an {@code Optional} describing (as if by
   * {@link Optional#ofNullable}) the result of applying the given mapping function to
   * the value, otherwise returns an empty {@code Optional}.
   *
   * <p>If the mapping function returns a {@code null} result then this method
   * returns an empty {@code Optional}.
   *
   * @param mapper the mapping function to apply to a value, if present
   * @param <U>    The type of the value returned from the mapping function
   * @return an {@code Optional} describing the result of applying a mapping
   * function to the value of this {@code Optional}, if a value is
   * present, otherwise an empty {@code Optional}
   * @throws NullPointerException if the mapping function is {@code null}
   * @apiNote This method supports post-processing on {@code Optional} values, without
   * the need to explicitly check for a return status.  For example, the
   * following code traverses a stream of URIs, selects one that has not
   * yet been processed, and creates a path from that URI, returning
   * an {@code Optional<Path>}:
   *
   * <pre>{@code
   *     Optional<Path> p =
   *         uris.stream().filter(uri -> !isProcessedYet(uri))
   *                       .findFirst()
   *                       .map(Paths::get);
   * }</pre>
   * <p>
   * Here, {@code findFirst} returns an {@code Optional<URI>}, and then
   * {@code map} returns an {@code Optional<Path>} for the desired
   * URI if one exists.
   */
  default <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    return opt().map(mapper);
  }

  /**
   * If a value is present, returns the result of applying the given
   * {@code Optional}-bearing mapping function to the value, otherwise returns
   * an empty {@code Optional}.
   *
   * <p>This method is similar to {@link #map(Function)}, but the mapping
   * function is one whose result is already an {@code Optional}, and if
   * invoked, {@code flatMap} does not wrap it within an additional
   * {@code Optional}.
   *
   * @param <U>    The type of value of the {@code Optional} returned by the
   *               mapping function
   * @param mapper the mapping function to apply to a value, if present
   * @return the result of applying an {@code Optional}-bearing mapping
   * function to the value of this {@code Optional}, if a value is
   * present, otherwise an empty {@code Optional}
   * @throws NullPointerException if the mapping function is {@code null} or
   *                              returns a {@code null} result
   */
  default <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
    Objects.requireNonNull(mapper);
    return opt().flatMap(mapper);
  }

  /**
   * If a value is present, returns an {@code Optional} describing the value,
   * otherwise returns an {@code Optional} produced by the supplying function.
   *
   * @param supplier the supplying function that produces an {@code Optional}
   *                 to be returned
   * @return returns an {@code Optional} describing the value of this
   * {@code Optional}, if a value is present, otherwise an
   * {@code Optional} produced by the supplying function.
   * @throws NullPointerException if the supplying function is {@code null} or
   *                              produces a {@code null} result
   * @since 9
   */
  default Optional<T> or(Supplier<? extends Optional<? extends T>> supplier) {
    Objects.requireNonNull(supplier);
    return opt().or(supplier);
  }

  /**
   * If a value is present, returns a sequential {@link Stream} containing
   * only that value, otherwise returns an empty {@code Stream}.
   *
   * @return the optional value as a {@code Stream}
   * @apiNote This method can be used to transform a {@code Stream} of optional
   * elements to a {@code Stream} of present value elements:
   * <pre>{@code
   *     Stream<Optional<T>> os = ..
   *     Stream<T> s = os.flatMap(Optional::stream)
   * }</pre>
   * @since 9
   */
  default Stream<T> stream() {
    return opt().stream();
  }

  /**
   * If a value is present, returns the value, otherwise returns
   * {@code other}.
   *
   * @param other the value to be returned, if no value is present.
   *              May be {@code null}.
   * @return the value, if present, otherwise {@code other}
   */
  default T orElse(T other) {
    return opt().orElse(other);
  }

  /**
   * If a value is present, returns the value, otherwise returns the result
   * produced by the supplying function.
   *
   * @param supplier the supplying function that produces a value to be returned
   * @return the value, if present, otherwise the result produced by the
   * supplying function
   * @throws NullPointerException if no value is present and the supplying
   *                              function is {@code null}
   */
  default T orElseGet(Supplier<? extends T> supplier) {
    return opt().orElseGet(supplier);
  }

  /**
   * If a value is present, returns the value, otherwise throws
   * {@code NoSuchElementException}.
   *
   * @return the non-{@code null} value described by this {@code Optional}
   * @throws NoSuchElementException if no value is present
   * @since 10
   */
  default T orElseThrow() {
    return opt().orElseThrow();
  }

  /**
   * If a value is present, returns the value, otherwise throws an exception
   * produced by the exception supplying function.
   *
   * @param <X>               Type of the exception to be thrown
   * @param exceptionSupplier the supplying function that produces an
   *                          exception to be thrown
   * @return the value, if present
   * @throws X                    if no value is present
   * @throws NullPointerException if no value is present and the exception
   *                              supplying function is {@code null}
   * @apiNote A method reference to the exception constructor with an empty argument
   * list can be used as the supplier. For example,
   * {@code IllegalStateException::new}
   */
  default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    return opt().orElseThrow(exceptionSupplier);
  }

}
