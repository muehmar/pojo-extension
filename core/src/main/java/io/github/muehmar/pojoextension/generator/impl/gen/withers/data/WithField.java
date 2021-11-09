package io.github.muehmar.pojoextension.generator.impl.gen.withers.data;

import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import java.util.Objects;

public class WithField {
  private final Pojo pojo;
  private final PojoField field;

  private WithField(Pojo pojo, PojoField field) {
    this.pojo = pojo;
    this.field = field;
  }

  public static WithField of(Pojo pojo, PojoField field) {
    return new WithField(pojo, field);
  }

  public Pojo getPojo() {
    return pojo;
  }

  public PojoField getField() {
    return field;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WithField withField = (WithField) o;
    return Objects.equals(pojo, withField.pojo) && Objects.equals(field, withField.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pojo, field);
  }

  @Override
  public String toString() {
    return "WithField{" + "pojo=" + pojo + ", field=" + field + '}';
  }
}
