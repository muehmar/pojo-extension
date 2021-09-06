package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;

public class Type {
  private final Name name;
  private final PackageName pkg;
  private final PList<Type> generics;

  public Type(Name name, PackageName pkg, PList<Type> generics) {
    this.name = name;
    this.pkg = pkg;
    this.generics = generics;
  }

  public Name getName() {
    return name;
  }

  public Name getQualifiedName() {
    return name.prefix(pkg.asString() + ".");
  }

  public PackageName getPackage() {
    return pkg;
  }

  public PList<Type> getGenerics() {
    return generics;
  }
}
