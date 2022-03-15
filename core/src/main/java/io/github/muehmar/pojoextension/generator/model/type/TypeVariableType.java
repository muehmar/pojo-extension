package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Name;

@PojoExtension
@SuppressWarnings("java:S2160")
public class TypeVariableType extends TypeVariableTypeBase implements SpecificType {
  private final Name name;
  private final PList<Type> upperBounds;

  TypeVariableType(Name name, PList<Type> upperBounds) {
    this.name = name;
    this.upperBounds = upperBounds;
  }

  public static TypeVariableType ofName(Name name) {
    return new TypeVariableType(name, PList.empty());
  }

  public static TypeVariableType ofNameAndUpperBounds(Name name, PList<Type> upperBounds) {
    return new TypeVariableType(name, upperBounds);
  }

  @Override
  public Name getName() {
    return name;
  }

  @Override
  public TypeKind getKind() {
    return TypeKind.TYPE_VAR;
  }

  @Override
  public Name getTypeDeclaration() {
    return getName();
  }

  @Override
  public PList<Name> getImports() {
    return upperBounds.flatMap(Type::getImports);
  }

  public Name getTypeVariableDeclaration() {
    final String upperBoundsDeclaration = upperBounds.map(Type::getTypeDeclaration).mkString(" & ");
    final String generic = Strings.surroundIfNotEmpty(" extends ", upperBoundsDeclaration, "");
    return name.append(generic);
  }

  public PList<Type> getUpperBounds() {
    return upperBounds;
  }
}
