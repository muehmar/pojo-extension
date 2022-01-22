package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;

@PojoExtension
@SuppressWarnings("java:S2160")
public class FullBuilderField extends FullBuilderFieldBase {
  private final BuilderField builderField;
  private final PList<FieldBuilderMethod> fieldBuilderMethods;

  public FullBuilderField(
      BuilderField builderField, PList<FieldBuilderMethod> fieldBuilderMethods) {
    this.builderField = builderField;
    this.fieldBuilderMethods = fieldBuilderMethods;
  }

  public BuilderField getBuilderField() {
    return builderField;
  }

  public PList<FieldBuilderMethod> getFieldBuilderMethods() {
    return fieldBuilderMethods;
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

  public PList<FieldBuilderField> getFieldBuilderFields() {
    return fieldBuilderMethods.map(
        fieldBuilderMethod -> new FieldBuilderField(builderField, fieldBuilderMethod));
  }
}
