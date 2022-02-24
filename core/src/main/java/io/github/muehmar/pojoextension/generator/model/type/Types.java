package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PackageName;
import java.util.Optional;

public class Types {
  private Types() {}

  public static Type primitiveDouble() {
    return new Type(PrimitiveType.DOUBLE);
  }

  public static Type primitiveInt() {
    return new Type(PrimitiveType.INT);
  }

  public static Type primitiveLong() {
    return new Type(PrimitiveType.LONG);
  }

  public static Type primitiveChar() {
    return new Type(PrimitiveType.CHAR);
  }

  public static Type primitiveShort() {
    return new Type(PrimitiveType.SHORT);
  }

  public static Type primitiveByte() {
    return new Type(PrimitiveType.BYTE);
  }

  public static Type primitiveBoolean() {
    return new Type(PrimitiveType.BOOLEAN);
  }

  public static Type primitiveFloat() {
    return new Type(PrimitiveType.FLOAT);
  }

  public static Type voidType() {
    return new Type(
        DeclaredType.fromNameAndPackage(Name.fromString("Void"), PackageName.javaLang()));
  }

  public static Type string() {
    return declaredType(Name.fromString("String"), PackageName.javaLang());
  }

  public static Type integer() {
    return declaredType(Name.fromString("Integer"), PackageName.javaLang());
  }

  public static Type booleanClass() {
    return declaredType(Name.fromString("Boolean"), PackageName.javaLang());
  }

  public static Type optional(Type value) {
    return new Type(DeclaredType.optional(value));
  }

  public static Type map(Type key, Type value) {
    return new Type(
        DeclaredType.of(Name.fromString("Map"), PackageName.javaUtil(), PList.of(key, value)));
  }

  public static Type list(Type value) {
    return new Type(
        DeclaredType.of(Name.fromString("List"), PackageName.javaUtil(), PList.single(value)));
  }

  public static Type array(Type itemType) {
    return new Type(ArrayType.fromItemType(itemType));
  }

  public static Type comparable(Type objType) {
    return new Type(
        DeclaredType.of(Name.fromString("Comparable"), PackageName.javaLang(), objType));
  }

  public static Type typeVariable(Name name) {
    return new Type(TypeVariableType.ofName(name));
  }

  public static Type declaredType(Name name, PackageName pkg) {
    return new Type(DeclaredType.fromNameAndPackage(name, pkg));
  }

  public static Type declaredType(
      Name name, Optional<PackageName> pkg, PList<Type> typeParameters) {
    return new Type(DeclaredType.of(name, pkg, typeParameters));
  }
}
