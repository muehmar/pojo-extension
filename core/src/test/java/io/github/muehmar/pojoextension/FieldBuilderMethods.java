package io.github.muehmar.pojoextension;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.data.FieldBuilderMethodBuilder;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;

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
