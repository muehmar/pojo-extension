package io.github.muehmar.pojoextension.generator.data.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Name;

@PojoExtension
@SuppressWarnings("java:S2160")
public class ArrayType extends ArrayTypeBase implements SpecificType {
  private final Type itemType;

  ArrayType(Type itemType) {
    this.itemType = itemType;
  }

  public static ArrayType fromItemType(Type itemType) {
    return new ArrayType(itemType);
  }

  @Override
  public Name getName() {
    return getTypeDeclaration();
  }

  @Override
  public TypeKind getKind() {
    return TypeKind.ARRAY;
  }

  @Override
  public Name getTypeDeclaration() {
    return itemType.getTypeDeclaration().append("[]");
  }

  @Override
  public PList<Name> getImports() {
    return itemType.getImports();
  }

  public Type getItemType() {
    return itemType;
  }
}
