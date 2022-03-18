package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Name;
import lombok.Value;

@Value
@PojoExtension
public class ArrayType implements ArrayTypeExtension, SpecificType {
  Type itemType;
  boolean isVarargs;

  public static ArrayType fromItemType(Type itemType) {
    return new ArrayType(itemType, false);
  }

  public static ArrayType varargs(Type itemType) {
    return new ArrayType(itemType, true);
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
    final String suffix = isVarargs ? "..." : "[]";
    return itemType.getTypeDeclaration().append(suffix);
  }

  @Override
  public PList<Name> getImports() {
    return itemType.getImports();
  }
}
