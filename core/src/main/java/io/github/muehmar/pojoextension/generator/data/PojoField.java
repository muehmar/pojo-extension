package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoField extends PojoFieldExtension {
  private final Name name;
  private final Type type;
  private final boolean required;

  public PojoField(Name name, Type type, boolean required) {
    this.name = name;
    this.type = type;
    this.required = required;
  }

  public Type getType() {
    return type;
  }

  public Name getName() {
    return name;
  }

  public boolean isRequired() {
    return required;
  }

  public boolean isOptional() {
    return !isRequired();
  }

  @Override
  public String toString() {
    return "PojoField{" + "type=" + type + ", name=" + name + ", required=" + required + '}';
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
