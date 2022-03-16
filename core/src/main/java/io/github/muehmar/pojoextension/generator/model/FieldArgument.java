package io.github.muehmar.pojoextension.generator.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import lombok.Value;

@Value
@PojoExtension
public class FieldArgument implements FieldArgumentExtension {
  PojoField field;
  Argument argument;
  OptionalFieldRelation relation;

  /** Returns the relation from the field to the argument */
  public OptionalFieldRelation getRelation() {
    return relation;
  }
}
