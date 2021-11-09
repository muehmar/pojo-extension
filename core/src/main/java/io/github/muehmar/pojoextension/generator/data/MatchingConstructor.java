package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;

@PojoExtension
public class MatchingConstructor extends MatchingConstructorExtension {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MatchingConstructor that = (MatchingConstructor) o;
    return Objects.equals(constructor, that.constructor)
        && Objects.equals(fieldArguments, that.fieldArguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(constructor, fieldArguments);
  }

  @Override
  public String toString() {
    return "MatchingConstructor{"
        + "constructor="
        + constructor
        + ", fieldArguments="
        + fieldArguments
        + '}';
  }
}
