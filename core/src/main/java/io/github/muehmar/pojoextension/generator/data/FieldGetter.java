package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
@SuppressWarnings("java:S2160")
public class FieldGetter extends FieldGetterBase {
  private final Getter getter;
  private final PojoField field;
  private final OptionalFieldRelation relation;

  FieldGetter(Getter getter, PojoField field, OptionalFieldRelation relation) {
    this.getter = getter;
    this.field = field;
    this.relation = relation;
  }

  public static FieldGetter of(Getter getter, PojoField field, OptionalFieldRelation relation) {
    return new FieldGetter(getter, field, relation);
  }

  public Getter getGetter() {
    return getter;
  }

  public PojoField getField() {
    return field;
  }

  public OptionalFieldRelation getRelation() {
    return relation;
  }
}
