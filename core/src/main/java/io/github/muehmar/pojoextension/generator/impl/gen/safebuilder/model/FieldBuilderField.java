package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import lombok.Value;

@Value
@PojoExtension
public class FieldBuilderField implements FieldBuilderFieldExtension {
  BuilderField builderField;
  FieldBuilderMethod fieldBuilderMethod;

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
