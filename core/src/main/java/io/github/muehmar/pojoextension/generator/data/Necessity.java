package io.github.muehmar.pojoextension.generator.data;

import java.util.function.Function;
import java.util.function.Supplier;

public enum Necessity {
  REQUIRED(true),
  OPTIONAL(false);

  private final boolean required;

  Necessity(boolean required) {
    this.required = required;
  }

  public boolean isRequired() {
    return required;
  }

  public boolean isOptional() {
    return !isRequired();
  }

  public <T> FoldOnOptional<T> onRequired(Supplier<T> s) {
    return new FoldOnOptional<>(onOptional -> required ? s.get() : onOptional.get());
  }

  public static class FoldOnOptional<T> {
    private final Function<Supplier<T>, T> f;

    public FoldOnOptional(Function<Supplier<T>, T> f) {
      this.f = f;
    }

    public T onOptional(Supplier<T> s) {
      return f.apply(s);
    }
  }
}
