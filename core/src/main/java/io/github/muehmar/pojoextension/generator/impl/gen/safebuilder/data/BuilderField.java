package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;

@PojoExtension
@SuppressWarnings("java:S2160")
public class BuilderField extends BuilderFieldBase {
  private final Pojo pojo;
  private final PojoField field;
  private final int index;

  public BuilderField(Pojo pojo, PojoField field, int index) {
    this.pojo = pojo;
    this.field = field;
    this.index = index;
  }

  public Pojo getPojo() {
    return pojo;
  }

  public PojoField getField() {
    return field;
  }

  public int getIndex() {
    return index;
  }
}
