package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import io.github.muehmar.pojoextension.generator.data.PojoField;
import java.util.Objects;

public class SafeBuilderPojoField {
  private final PojoField field;
  private final int index;

  public SafeBuilderPojoField(PojoField field, int index) {
    this.field = field;
    this.index = index;
  }

  public PojoField getField() {
    return field;
  }

  public int getIndex() {
    return index;
  }

  public SafeBuilderPojoField withFieldIndex(int index) {
    return new SafeBuilderPojoField(field, index);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SafeBuilderPojoField that = (SafeBuilderPojoField) o;
    return index == that.index && Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, index);
  }
}
