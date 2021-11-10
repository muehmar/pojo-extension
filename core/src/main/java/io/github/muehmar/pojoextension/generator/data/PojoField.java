package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoField extends PojoFieldExtension {
  private final Name name;
  private final Type type;
  private final Necessity necessity;

  public PojoField(Name name, Type type, Necessity necessity) {
    this.name = name;
    this.type = type;
    this.necessity = necessity;
  }

  public Type getType() {
    return type;
  }

  public Name getName() {
    return name;
  }

  public Necessity getNecessity() {
    return necessity;
  }

  public boolean isRequired() {
    return necessity.isRequired();
  }

  public boolean isOptional() {
    return necessity.isOptional();
  }

  @Override
  public String toString() {
    return "PojoField{" + "type=" + type + ", name=" + name + ", required=" + necessity + '}';
  }

  @FunctionalInterface
  public interface OnExactMatch<T> {
    T apply(PojoField field, Argument argument);
  }

  @FunctionalInterface
  public interface OnOptionalMatch<T> {
    T apply(PojoField field, Argument argument);
  }

  @FunctionalInterface
  public interface OnNoMatch<T> {
    T apply(PojoField field, Argument argument);
  }
}
