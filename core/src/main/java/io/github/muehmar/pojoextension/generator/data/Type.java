package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Type {
  private final Name name;
  private final PackageName pkg;
  private final PList<Type> typeParameters;
  private final boolean isArray;

  private static final Pattern QUALIFIED_CLASS_NAME_PATTERN =
      Pattern.compile("([.A-Za-z_$0-9]*)\\.([A-Za-z_$0-9]*)");

  private static final PList<Type> primitiveTypes =
      PList.of("int", "byte", "short", "long", "float", "double", "boolean", "char")
          .map(Type::primitive);

  public Type(Name name, PackageName pkg, PList<Type> typeParameters, boolean isArray) {
    this.name = name;
    this.pkg = pkg;
    this.typeParameters = typeParameters;
    this.isArray = isArray;
  }

  public static PList<Type> allPrimitives() {
    return primitiveTypes;
  }

  public static Type primitive(String primitive) {
    return new Type(Name.fromString(primitive), PackageName.javaLang(), PList.empty(), false);
  }

  public static Type string() {
    return new Type(Name.fromString("String"), PackageName.javaLang(), PList.empty(), false);
  }

  public static Type integer() {
    return new Type(Name.fromString("Integer"), PackageName.javaLang(), PList.empty(), false);
  }

  public static Type primitiveDouble() {
    return primitive("double");
  }

  public static Type optional(Type value) {
    return new Type(
        Name.fromString("Optional"), PackageName.javaUtil(), PList.single(value), false);
  }

  public static Type map(Type key, Type value) {
    return new Type(Name.fromString("Map"), PackageName.javaUtil(), PList.of(key, value), false);
  }

  public static Type fromClassName(String className) {
    final Matcher matcher = QUALIFIED_CLASS_NAME_PATTERN.matcher(className);
    if (matcher.find()) {
      final PackageName packageName = PackageName.fromString(matcher.group(1));
      final Name name = Name.fromString(matcher.group(2));
      return new Type(name, packageName, PList.empty(), false);
    }
    return primitiveTypes
        .find(t -> t.getName().asString().equals(className))
        .orElseThrow(() -> new IllegalArgumentException("Not a valid classname: " + className));
  }

  public static Type from(Name name, PackageName pkg) {
    return new Type(name, pkg, PList.empty(), false);
  }

  public Type withTypeParameters(PList<Type> typeParameters) {
    return new Type(name, pkg, typeParameters, isArray);
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
    return getName()
        .append(formattedTypeParameters.map(Name::asString).orElse(""))
        .append(isArray ? "[]" : "");
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

  public boolean isArray() {
    return isArray;
  }

  public boolean isNotArray() {
    return !isArray();
  }

  public Type withIsArray(boolean isArray) {
    return new Type(name, pkg, typeParameters, isArray);
  }

  public boolean isPrimitive() {
    return primitiveTypes.find(this::equals).isPresent();
  }

  /**
   * Runs the given function in case this type is an {@link Optional} with the single type parameter
   * of the optional.
   */
  public <T> Optional<T> onOptional(Function<Type, T> f) {
    final Type self = this;
    return typeParameters.headOption().filter(type -> optional(type).equals(self)).map(f);
  }

  public boolean equalsIgnoreTypeParameters(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Type type = (Type) o;
    return Objects.equals(name, type.name)
        && Objects.equals(pkg, type.pkg)
        && Objects.equals(isArray, type.isArray);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Type type = (Type) o;
    return isArray == type.isArray
        && Objects.equals(name, type.name)
        && Objects.equals(pkg, type.pkg)
        && Objects.equals(typeParameters, type.typeParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pkg, typeParameters, isArray);
  }

  @Override
  public String toString() {
    return "Type{"
        + "name="
        + name
        + ", pkg="
        + pkg
        + ", typeParameters="
        + typeParameters
        + ", isArray="
        + isArray
        + '}';
  }
}
