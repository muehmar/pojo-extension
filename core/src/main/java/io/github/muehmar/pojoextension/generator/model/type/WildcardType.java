package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Name;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class WildcardType implements SpecificType {
  private WildcardType() {}

  public static WildcardType create() {
    return new WildcardType();
  }

  @Override
  public Name getName() {
    return Name.fromString("?");
  }

  @Override
  public TypeKind getKind() {
    return TypeKind.WILDCARD;
  }

  @Override
  public Name getTypeDeclaration() {
    return Name.fromString("?");
  }

  @Override
  public PList<Name> getImports() {
    return PList.empty();
  }
}
