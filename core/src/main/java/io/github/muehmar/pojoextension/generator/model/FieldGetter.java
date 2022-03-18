package io.github.muehmar.pojoextension.generator.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import lombok.Value;

@Value
@PojoExtension
public class FieldGetter implements FieldGetterExtension {
  Getter getter;
  PojoField field;
  OptionalFieldRelation relation;

  public static FieldGetter of(Getter getter, PojoField field, OptionalFieldRelation relation) {
    return new FieldGetter(getter, field, relation);
  }
}
