package io.github.muehmar.pojoextension.generator.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoAndField extends PojoAndFieldBase {
  private final Pojo pojo;
  private final PojoField field;

  public PojoAndField(Pojo pojo, PojoField field) {
    this.pojo = pojo;
    this.field = field;
  }

  public static PojoAndField of(Pojo pojo, PojoField field) {
    return new PojoAndField(pojo, field);
  }

  public Pojo getPojo() {
    return pojo;
  }

  public PojoField getField() {
    return field;
  }

  public FieldGetter getMatchingGetterOrThrow() {
    return pojo.getMatchingGetterOrThrow(field);
  }
}
