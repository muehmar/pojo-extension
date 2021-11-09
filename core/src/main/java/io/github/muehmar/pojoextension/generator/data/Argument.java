package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;

import java.util.Objects;
import java.util.Optional;

public class Argument {
  private final Name name;
  private final Type type;

  public Argument(Name name, Type type) {
    this.name = name;
    this.type = type;
  }

  public static Argument of(Name name, Type type) {
    return new Argument(name, type);
  }

  public Name getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  /** Returns the relation from the field to the argument */
  public Optional<OptionalFieldRelation> getRelationFromField(PojoField field) {
    if (field.isRequired()) {
      return type.equals(field.getType()) ? Optional.of(SAME_TYPE) : Optional.empty();
    }

    return field.getType().getRelation(this.getType());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Argument argument = (Argument) o;
    return Objects.equals(name, argument.name) && Objects.equals(type, argument.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }

  @Override
  public String toString() {
    return "Argument{" + "name=" + name + ", type=" + type + '}';
  }
}
