package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
@SuppressWarnings("java:S2160")
public class MatchingConstructor extends MatchingConstructorBase {
  private final Constructor constructor;
  private final PList<FieldArgument> fieldArguments;

  public MatchingConstructor(Constructor constructor, PList<FieldArgument> fieldArguments) {
    this.constructor = constructor;
    this.fieldArguments = fieldArguments;
  }

  public Constructor getConstructor() {
    return constructor;
  }

  public PList<FieldArgument> getFieldArguments() {
    return fieldArguments;
  }
}
