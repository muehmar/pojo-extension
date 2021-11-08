package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import io.github.muehmar.pojoextension.generator.data.FieldArgument;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation;
import java.util.Objects;

/** Represents */
class FinalConstructorArgument {
  private final String fieldString;
  private final OptionalFieldRelation relation;

  public FinalConstructorArgument(String fieldString, OptionalFieldRelation relation) {
    this.fieldString = fieldString;
    this.relation = relation;
  }

  public static FinalConstructorArgument ofFieldVariable(
      FieldVariable fieldVariable, FieldArgument fieldArgument) {
    return new FinalConstructorArgument(
        fieldVariable.getField().getName().asString(),
        fieldVariable.getRelation().andThen(fieldArgument.getRelation()));
  }

  public static FinalConstructorArgument ofGetter(
      FieldGetter fieldGetter, FieldArgument fieldArgument) {
    final OptionalFieldRelation relation =
        fieldGetter.getRelation().andThen(fieldArgument.getRelation());
    return new FinalConstructorArgument(
        fieldGetter.getGetter().getName().prefix("self.").append("()").asString(), relation);
  }

  public String getFieldString() {
    return fieldString;
  }

  public OptionalFieldRelation getRelation() {
    return relation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FinalConstructorArgument finalConstructorArgument = (FinalConstructorArgument) o;
    return Objects.equals(fieldString, finalConstructorArgument.fieldString)
        && relation == finalConstructorArgument.relation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldString, relation);
  }

  @Override
  public String toString() {
    return "Field{" + "fieldString='" + fieldString + '\'' + ", relation=" + relation + '}';
  }
}
