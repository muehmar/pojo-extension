package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;

@PojoExtension
public class PojoField extends PojoFieldExtension {
  private final Type type;
  private final Name name;
  private final boolean required;

  public PojoField(Type type, Name name, boolean required) {
    this.type = type;
    this.name = name;
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

  public PojoField withRequired(boolean required) {
    return new PojoField(type, name, required);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PojoField that = (PojoField) o;
    return required == that.required
        && Objects.equals(type, that.type)
        && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, required);
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
