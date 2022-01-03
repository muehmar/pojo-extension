package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.Booleans.not;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import java.util.function.UnaryOperator;

@PojoExtension
@SuppressWarnings("java:S2160")
public class Getter extends GetterExtension {
  private final Name name;
  private final Type returnType;
  private final Optional<Name> fieldName;

  public Getter(Name name, Type returnType, Optional<Name> fieldName) {
    this.name = name;
    this.returnType = returnType;
    this.fieldName = fieldName;
  }

  public static Optional<Name> noFieldName() {
    return Optional.empty();
  }

  public static Name javaBeanGetterName(PojoField field) {
    if (field.getType().equals(Type.primitiveBoolean())) {
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

  public Name getName() {
    return name;
  }

  public Type getReturnType() {
    return returnType;
  }

  public Optional<Name> getFieldName() {
    return fieldName;
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
          ? Optional.of(FieldGetter.of(this, field, SAME_TYPE))
          : Optional.empty();
    }

    return returnType.getRelation(field.getType()).map(rel -> FieldGetter.of(self, field, rel));
  }
}
