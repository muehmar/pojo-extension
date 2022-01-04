package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoAndField;
import io.github.muehmar.pojoextension.generator.data.PojoField;

@PojoExtension
public class SafeBuilderPojoField extends SafeBuilderPojoFieldBase {
  private final PojoAndField pojoAndField;
  private final int index;

  public SafeBuilderPojoField(PojoAndField pojoAndField, int index) {
    this.pojoAndField = pojoAndField;
    this.index = index;
  }

  public SafeBuilderPojoField(Pojo pojo, PojoField field, int index) {
    this(new PojoAndField(pojo, field), index);
  }

  public PojoAndField getPojoAndField() {
    return pojoAndField;
  }

  public PojoField getField() {
    return pojoAndField.getField();
  }

  public Pojo getPojo() {
    return pojoAndField.getPojo();
  }

  public int getIndex() {
    return index;
  }
}
