package kz.greetgo.cached.core.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class DoOnce {

  private final Predicate<Throwable> commitOnError;

  public DoOnce(Predicate<Throwable> commitOnError) {
    this.commitOnError = commitOnError;
  }

  public interface Once {
    void operation() throws Throwable;
  }

  private final AtomicBoolean done = new AtomicBoolean(false);

  public void doOnce(Once once) {
    if (done.get()) {
      return;
    }
    synchronized (done) {
      if (done.get()) {
        return;
      }
      try {
        once.operation();
        done.set(true);
      } catch (Throwable throwable) {
        if (commitOnError.test(throwable)) {
          done.set(true);
        }
        throw new RuntimeException(throwable);
      }
    }
  }


}
