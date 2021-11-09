package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import java.util.Objects;

public class FieldVariable {
  private final Pojo pojo;
  private final PojoField field;
  private final OptionalFieldRelation relation;

  public FieldVariable(Pojo pojo, PojoField field, OptionalFieldRelation relation) {
    this.pojo = pojo;
    this.field = field;
    this.relation = relation;
  }

  public Pojo getPojo() {
    return pojo;
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
    FieldVariable that = (FieldVariable) o;
    return Objects.equals(pojo, that.pojo)
        && Objects.equals(field, that.field)
        && relation == that.relation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pojo, field, relation);
  }

  @Override
  public String toString() {
    return "FieldVariable{" + "pojo=" + pojo + ", field=" + field + ", relation=" + relation + '}';
  }
}
