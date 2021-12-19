package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
public class Generic extends GenericExtension {
  private final Name typeVariable;
  private final PList<Type> upperBounds;

  public Generic(Name typeVariable, PList<Type> upperBounds) {
    this.typeVariable = typeVariable;
    this.upperBounds = upperBounds;
  }

  public Name getTypeVariable() {
    return typeVariable;
  }

  public PList<Type> getUpperBounds() {
    return upperBounds;
  }

  public Name getTypeDeclaration() {
    final String upperBoundsDeclaration =
        upperBounds.map(Type::getTypeDeclaration).map(Name::asString).mkString(", ");
    return typeVariable.append(Strings.surroundIfNotEmpty(" extends ", upperBoundsDeclaration, ""));
  }
}
