package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.Booleans.not;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;
import java.util.Optional;

@PojoExtension
public class Getter extends GetterExtension {
  private final Name name;
  private final Type returnType;

  public Getter(Name name, Type returnType) {
    this.name = name;
    this.returnType = returnType;
  }

  public static Name getterName(PojoField field) {
    final String prefix = field.getType().equals(Type.primitiveBoolean()) ? "is" : "get";
    return field.getName().javaBeansName().prefix(prefix);
  }

  public Name getName() {
    return name;
  }

  public Type getReturnType() {
    return returnType;
  }

  public Optional<FieldGetter> getFieldGetter(PojoField field) {
    if (not(name.equals(getterName(field)))) {
      return Optional.empty();
    }

    final Getter self = this;
    if (field.isRequired()) {
      return returnType.equals(field.getType())
          ? Optional.of(FieldGetter.of(this, field, SAME_TYPE))
          : Optional.empty();
    }

    return returnType
        .getRelation(field.getType())
        .map(relation -> FieldGetter.of(self, field, relation));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Getter getter = (Getter) o;
    return Objects.equals(name, getter.name) && Objects.equals(returnType, getter.returnType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, returnType);
  }

  @Override
  public String toString() {
    return "Getter{" + "name=" + name + ", returnType=" + returnType + '}';
  }
}
