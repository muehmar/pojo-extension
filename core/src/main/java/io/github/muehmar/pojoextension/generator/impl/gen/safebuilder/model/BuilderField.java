package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import lombok.Value;

@Value
@PojoExtension
public class BuilderField implements BuilderFieldExtension {
  Pojo pojo;
  PojoField field;
  int index;
}
