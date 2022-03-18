package io.github.muehmar.pojoextension.generator.model;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import lombok.Value;

@Value
@PojoExtension
public class Generic implements GenericExtension {
  Name typeVariable;
  PList<Type> upperBounds;

  public Name getTypeDeclaration() {
    final String upperBoundsDeclaration =
        upperBounds.map(Type::getTypeDeclaration).map(Name::asString).mkString(" & ");
    return typeVariable.append(Strings.surroundIfNotEmpty(" extends ", upperBoundsDeclaration, ""));
  }
}
