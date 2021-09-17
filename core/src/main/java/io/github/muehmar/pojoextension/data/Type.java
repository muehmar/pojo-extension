package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Type {
  private final Name name;
  private final PackageName pkg;
  private final PList<Type> typeParameters;

  private static final Pattern QUALIFIED_CLASS_NAME_PATTERN =
      Pattern.compile("([.A-Za-z_$0-9]*)\\.([A-Za-z_$0-9]*)");

  public Type(Name name, PackageName pkg, PList<Type> typeParameters) {
    this.name = name;
    this.pkg = pkg;
    this.typeParameters = typeParameters;
  }

  public static Type string() {
    return new Type(Name.fromString("String"), PackageName.javaLang(), PList.empty());
  }

  public static Type integer() {
    return new Type(Name.fromString("Integer"), PackageName.javaLang(), PList.empty());
  }

  public static Type optional(Type value) {
    return new Type(Name.fromString("Optional"), PackageName.javaUtil(), PList.single(value));
  }

  public static Type map(Type key, Type value) {
    return new Type(Name.fromString("Map"), PackageName.javaUtil(), PList.of(key, value));
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

  public Type withTypeParameters(PList<Type> typeParameters) {
    return new Type(name, pkg, typeParameters);
  }

  public Name getName() {
    return name;
  }

  /** Returns the class name of this type including type parameters. */
  public Name getClassName() {
    final Optional<Name> formattedTypeParameters =
        typeParameters
            .map(Type::getClassName)
            .reduce((s1, s2) -> s1.append(",").append(s2))
            .map(s -> s.prefix("<").append(">"));
    return getName().append(formattedTypeParameters.map(Name::asString).orElse(""));
  }

  public Name getQualifiedName() {
    return name.prefix(pkg.asString() + ".");
  }

  public PackageName getPackage() {
    return pkg;
  }

  public PList<Type> getTypeParameters() {
    return typeParameters;
  }

  public boolean equalsIgnoreTypeParameters(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Type type = (Type) o;
    return Objects.equals(name, type.name) && Objects.equals(pkg, type.pkg);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Type type = (Type) o;
    return Objects.equals(name, type.name)
        && Objects.equals(pkg, type.pkg)
        && Objects.equals(typeParameters, type.typeParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pkg, typeParameters);
  }

  @Override
  public String toString() {
    return "Type{" + "name=" + name + ", pkg=" + pkg + ", typeParameters=" + typeParameters + '}';
  }
}
