package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Type {
  private final Name name;
  private final PackageName pkg;
  private final PList<Type> generics;

  private static final Pattern QUALIFIED_CLASS_NAME_PATTERN =
      Pattern.compile("([.A-Za-z_$0-9]*)\\.([A-Za-z_$0-9]*)$");

  public Type(Name name, PackageName pkg, PList<Type> generics) {
    this.name = name;
    this.pkg = pkg;
    this.generics = generics;
  }

  public static Type string() {
    return new Type(Name.fromString("String"), PackageName.javaLang(), PList.empty());
  }

  public static Type fromQualifiedClassName(String qualifiedClassName) {
    final Matcher matcher = QUALIFIED_CLASS_NAME_PATTERN.matcher(qualifiedClassName);
    if (matcher.find()) {
      final PackageName packageName = PackageName.fromString(matcher.group(1));
      final Name name = Name.fromString(matcher.group(2));
      return new Type(name, packageName, PList.empty());
    }
    throw new IllegalArgumentException("Not a valid qualified classname: " + qualifiedClassName);
  }

  public static Type from(Name name, PackageName pkg) {
    return new Type(name, pkg, PList.empty());
  }

  public Type withGenerics(PList<Type> generics) {
    return new Type(name, pkg, generics);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Type type = (Type) o;
    return Objects.equals(name, type.name)
        && Objects.equals(pkg, type.pkg)
        && Objects.equals(generics, type.generics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pkg, generics);
  }

  @Override
  public String toString() {
    return "Type{" + "name=" + name + ", pkg=" + pkg + ", generics=" + generics + '}';
  }
}
