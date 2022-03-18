package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import lombok.Value;

@Value
@PojoExtension
public class SafeBuilderPojoField implements SafeBuilderPojoFieldExtension {
  Pojo pojo;
  PojoField field;
  PList<FieldBuilderMethod> fieldBuilderMethods;
  int index;
}
