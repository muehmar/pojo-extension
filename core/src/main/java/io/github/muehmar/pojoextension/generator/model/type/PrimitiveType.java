package io.github.muehmar.pojoextension.generator.model.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PackageName;

public enum PrimitiveType implements SpecificType {
  INT("int"),
  BYTE("byte"),
  SHORT("short"),
  LONG("long"),
  FLOAT("float"),
  DOUBLE("double"),
  BOOLEAN("boolean"),
  CHAR("char");

  private final Name name;

  PrimitiveType(String name) {
    this.name = Name.fromString(name);
  }

  @Override
  public Name getName() {
    return name;
  }

  @Override
  public TypeKind getKind() {
    return TypeKind.PRIMITIVE;
  }

  @Override
  public Name getTypeDeclaration() {
    return name;
  }

  @Override
  public PList<Name> getImports() {
    return PList.single(PackageName.javaLang().qualifiedName(name));
  }
}
