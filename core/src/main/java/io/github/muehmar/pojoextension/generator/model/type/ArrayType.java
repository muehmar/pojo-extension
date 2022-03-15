package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Name;

@PojoExtension
@SuppressWarnings("java:S2160")
public class ArrayType extends ArrayTypeBase implements SpecificType {
  private final Type itemType;
  private final boolean isVarargs;

  ArrayType(Type itemType, boolean isVarargs) {
    this.itemType = itemType;
    this.isVarargs = isVarargs;
  }

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

  public Type getItemType() {
    return itemType;
  }

  public boolean isVarargs() {
    return isVarargs;
  }
}
