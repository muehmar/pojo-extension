package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
public class PojoAndField extends PojoAndFieldBase {
  private final Pojo pojo;
  private final PojoField field;

  public PojoAndField(Pojo pojo, PojoField field) {
    this.pojo = pojo;
    this.field = field;
  }

  public Pojo getPojo() {
    return pojo;
  }

  public PojoField getField() {
    return field;
  }
}
