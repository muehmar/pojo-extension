package io.github.muehmar.pojoextension;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethodBuilder;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PojoField;

public class FieldBuilderMethods {
  private FieldBuilderMethods() {}

  public static FieldBuilderMethod forField(PojoField field, Name methodName, Argument argument) {
    return FieldBuilderMethodBuilder.create()
        .fieldName(field.getName())
        .methodName(methodName)
        .returnType(field.getType())
        .arguments(PList.single(argument))
        .build();
  }
}
