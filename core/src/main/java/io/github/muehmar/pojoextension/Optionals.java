package io.github.muehmar.pojoextension;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Optionals {
  private Optionals() {}

  public static <T> void ifPresentOrElse(Optional<T> val, Consumer<T> c, Runnable r) {
    val.ifPresent(c);
    if (!val.isPresent()) {
      r.run();
    }
  }

  public static <T> Optional<T> or(Optional<T> o1, Optional<T> o2) {
    return or(o1, () -> o2);
  }

  public static <T> Optional<T> or(Optional<T> o1, Supplier<Optional<T>> o2) {
    return o1.map(Optional::of).orElseGet(o2);
  }
}
