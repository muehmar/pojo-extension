package io.github.muehmar.pojoextension.generator.model;

import static io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.SAME_TYPE;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class Argument implements ArgumentExtension {
  Name name;
  Type type;

  public static Argument of(Name name, Type type) {
    return new Argument(name, type);
  }

  public String formatted() {
    return String.format("%s %s", type.getTypeDeclaration(), name);
  }

  /** Returns the relation from the field to the argument */
  public Optional<OptionalFieldRelation> getRelationFromField(PojoField field) {
    if (field.isRequired()) {
      return type.equals(field.getType()) ? Optional.of(SAME_TYPE) : Optional.empty();
    }

    return field.getType().getRelation(this.getType());
  }
}
