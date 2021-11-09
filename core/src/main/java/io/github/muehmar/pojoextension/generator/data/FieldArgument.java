package io.github.muehmar.pojoextension.generator.data;

import java.util.Objects;

public class FieldArgument {
  private final PojoField field;
  private final Argument argument;
  private final OptionalFieldRelation relation;

  public FieldArgument(PojoField field, Argument argument, OptionalFieldRelation relation) {
    this.field = field;
    this.argument = argument;
    this.relation = relation;
  }

  public PojoField getField() {
    return field;
  }

  public Argument getArgument() {
    return argument;
  }

  /** Returns the relation from the field to the argument */
  public OptionalFieldRelation getRelation() {
    return relation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FieldArgument that = (FieldArgument) o;
    return Objects.equals(field, that.field)
        && Objects.equals(argument, that.argument)
        && relation == that.relation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, argument, relation);
  }

  @Override
  public String toString() {
    return "FieldArgument{"
        + "field="
        + field
        + ", argument="
        + argument
        + ", relation="
        + relation
        + '}';
  }
}
