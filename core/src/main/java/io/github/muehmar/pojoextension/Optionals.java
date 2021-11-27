package io.github.muehmar.pojoextension;

import java.util.Optional;
import java.util.function.Consumer;

public class Optionals {
  private Optionals() {}

  public static <T> void ifPresentOrElse(Optional<T> val, Consumer<T> c, Runnable r) {
    val.ifPresent(c);
    if (!val.isPresent()) {
      r.run();
    }
  }
}
