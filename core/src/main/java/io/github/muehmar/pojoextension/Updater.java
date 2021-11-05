package io.github.muehmar.pojoextension;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Updater<T> {
  private final Supplier<T> inst;

  private Updater(Supplier<T> inst) {
    this.inst = inst;
  }

  public static <T> Updater<T> initial(T inst) {
    return new Updater<>(() -> inst);
  }

  public Updater<T> update(UnaryOperator<T> update) {
    return updateConditionally(true, update);
  }

  public Updater<T> updateConditionally(boolean shouldUpdate, UnaryOperator<T> update) {
    return shouldUpdate ? new Updater<>(() -> update.apply(inst.get())) : this;
  }

  public Updater<T> updateOrElse(
      boolean shouldUpdate, UnaryOperator<T> update, UnaryOperator<T> orElse) {
    return shouldUpdate
        ? new Updater<>(() -> update.apply(inst.get()))
        : new Updater<>(() -> orElse.apply(inst.get()));
  }

  public T get() {
    return inst.get();
  }
}
