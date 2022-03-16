package io.github.muehmar.pojoextension.generator.model;

import static io.github.muehmar.pojoextension.Booleans.not;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.Value;

@Value
@PojoExtension
public class Getter implements GetterExtension {
  Name name;
  Type returnType;
  Optional<Name> fieldName;

  public static Optional<Name> noFieldName() {
    return Optional.empty();
  }

  public static Name javaBeanGetterName(PojoField field) {
    if (field.getType().equals(Types.primitiveBoolean())) {
      return primitiveBooleanGetterName(field);
    }
    return field.getName().javaBeansName().prefix("get");
  }

  private static Name primitiveBooleanGetterName(PojoField field) {
    final UnaryOperator<Name> createGetter = in -> in.javaBeansName().prefix("is");
    final Name fieldName = field.getName();
    final boolean isAlreadyGetterName =
        fieldName.length() > 2
            && createGetter
                .apply(Name.fromString(fieldName.asString().substring(2)))
                .equals(fieldName);

    return isAlreadyGetterName ? fieldName : createGetter.apply(fieldName);
  }

  public Optional<FieldGetter> getFieldGetter(PojoField field) {
    final boolean fieldNameMatches = fieldName.map(n -> n.equals(field.getName())).orElse(false);
    final boolean methodNameMatches =
        name.equals(javaBeanGetterName(field)) || name.equals(field.getName());

    if (not(methodNameMatches) && not(fieldNameMatches)) {
      return Optional.empty();
    }

    final Getter self = this;
    if (field.isRequired()) {
      return returnType.equals(field.getType())
          ? Optional.of(FieldGetter.of(this, field, OptionalFieldRelation.SAME_TYPE))
          : Optional.empty();
    }

    return returnType.getRelation(field.getType()).map(rel -> FieldGetter.of(self, field, rel));
  }
}
