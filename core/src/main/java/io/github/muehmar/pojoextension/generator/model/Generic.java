package io.github.muehmar.pojoextension.generator.model;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.type.Type;

@PojoExtension
@SuppressWarnings("java:S2160")
public class Generic extends GenericBase {
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
        upperBounds.map(Type::getTypeDeclaration).map(Name::asString).mkString(" & ");
    return typeVariable.append(Strings.surroundIfNotEmpty(" extends ", upperBoundsDeclaration, ""));
  }
}
