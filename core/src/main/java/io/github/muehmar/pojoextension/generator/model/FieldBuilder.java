package io.github.muehmar.pojoextension.generator.model;

import ch.bluecare.commons.data.NonEmptyList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import lombok.Value;

@Value
@PojoExtension
public class FieldBuilder implements FieldBuilderExtension {
  boolean disableDefaultMethods;
  NonEmptyList<FieldBuilderMethod> methods;

  public Name getFieldName() {
    return methods.head().getFieldName();
  }
}
