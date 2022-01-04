package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
@SuppressWarnings("java:S2160")
public class FieldArgument extends FieldArgumentBase {
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
}
