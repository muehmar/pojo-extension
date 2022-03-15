package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;

@PojoExtension
@SuppressWarnings("java:S2160")
public class FieldBuilderField extends FieldBuilderFieldBase {
  private final BuilderField builderField;
  private final FieldBuilderMethod fieldBuilderMethod;

  public FieldBuilderField(BuilderField builderField, FieldBuilderMethod fieldBuilderMethod) {
    this.builderField = builderField;
    this.fieldBuilderMethod = fieldBuilderMethod;
  }

  public BuilderField getBuilderField() {
    return builderField;
  }

  public FieldBuilderMethod getFieldBuilderMethod() {
    return fieldBuilderMethod;
  }

  public Pojo getPojo() {
    return builderField.getPojo();
  }

  public PojoField getField() {
    return builderField.getField();
  }

  public int getIndex() {
    return builderField.getIndex();
  }
}
