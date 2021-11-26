package io.github.muehmar.pojoextension;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Mapper<T> {
  private final Supplier<T> inst;

  private Mapper(Supplier<T> inst) {
    this.inst = inst;
  }

  public static <T> Mapper<T> initial(T inst) {
    return new Mapper<>(() -> inst);
  }

  public Mapper<T> map(UnaryOperator<T> update) {
    return mapConditionally(true, update);
  }

  public Mapper<T> mapConditionally(boolean shouldUpdate, UnaryOperator<T> update) {
    return shouldUpdate ? new Mapper<>(() -> update.apply(inst.get())) : this;
  }

  public T apply() {
    return inst.get();
  }
}
