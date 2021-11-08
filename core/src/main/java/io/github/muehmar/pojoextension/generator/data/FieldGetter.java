package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;

@PojoExtension
public class FieldGetter extends FieldGetterExtension {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FieldGetter that = (FieldGetter) o;
    return Objects.equals(getter, that.getter)
        && Objects.equals(field, that.field)
        && relation == that.relation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getter, field, relation);
  }

  @Override
  public String toString() {
    return "FieldGetter{"
        + "getter="
        + getter
        + ", field="
        + field
        + ", relation="
        + relation
        + '}';
  }
}
