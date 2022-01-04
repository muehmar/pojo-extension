package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class Argument extends ArgumentBase {
  private final Name name;
  private final Type type;

  public Argument(Name name, Type type) {
    this.name = name;
    this.type = type;
  }

  public static Argument of(Name name, Type type) {
    return new Argument(name, type);
  }

  public Name getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  /** Returns the relation from the field to the argument */
  public Optional<OptionalFieldRelation> getRelationFromField(PojoField field) {
    if (field.isRequired()) {
      return type.equals(field.getType()) ? Optional.of(SAME_TYPE) : Optional.empty();
    }

    return field.getType().getRelation(this.getType());
  }
}
