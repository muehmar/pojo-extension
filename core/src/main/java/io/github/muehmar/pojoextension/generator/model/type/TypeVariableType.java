package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Name;
import lombok.Value;

@Value
@PojoExtension
public class TypeVariableType implements TypeVariableTypeExtension, SpecificType {
  Name name;
  PList<Type> upperBounds;

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
}
