package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.WRAP_INTO_OPTIONAL;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.Getter;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PojoExtension
@SuppressWarnings("java:S2160")
public class Type extends TypeExtension {
  private final Name name;
  private final PackageName pkg;
  private final PList<Type> typeParameters;
  private final boolean isArray;

  private static final String PACKAGE_NAME_PATTERN = "[a-z][A-Za-z_$0-9]*";
  private static final Pattern QUALIFIED_CLASS_NAME_PATTERN =
      Pattern.compile(String.format("((%s\\.?)*)\\.([A-Z][A-Za-z_$0-9.]*)", PACKAGE_NAME_PATTERN));

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

  public static Type voidType() {
    return new Type(Name.fromString("Void"), PackageName.javaLang(), PList.empty(), false);
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

  public static Type booleanClass() {
    return new Type(Name.fromString("Boolean"), PackageName.javaLang(), PList.empty(), false);
  }

  public static Type primitiveDouble() {
    return primitive("double");
  }

  public static Type primitiveBoolean() {
    return primitive("boolean");
  }

  public static Type optional(Type value) {
    return new Type(
        Name.fromString("Optional"), PackageName.javaUtil(), PList.single(value), false);
  }

  public static Type map(Type key, Type value) {
    return new Type(Name.fromString("Map"), PackageName.javaUtil(), PList.of(key, value), false);
  }

  public static Type list(Type value) {
    return new Type(Name.fromString("List"), PackageName.javaUtil(), PList.single(value), false);
  }

  public static Type fromClassName(String className) {
    final Matcher matcher = QUALIFIED_CLASS_NAME_PATTERN.matcher(className);
    if (matcher.find()) {
      final int count = matcher.groupCount();
      final PackageName packageName = PackageName.fromString(matcher.group(1));
      final Name name = Name.fromString(matcher.group(count));
      return new Type(name, packageName, PList.empty(), false);
    }
    return primitiveTypes
        .find(t -> t.getName().asString().equals(className))
        .orElseThrow(() -> new IllegalArgumentException("Not a valid classname: " + className));
  }

  public static Type from(Name name, PackageName pkg) {
    return new Type(name, pkg, PList.empty(), false);
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

  /** Returns the qualified class names of this and all generic types if any. */
  public PList<Name> getAllQualifiedNames() {
    return PList.single(getQualifiedName())
        .concat(typeParameters.flatMap(Type::getAllQualifiedNames));
  }

  @Getter("pkg")
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

  public boolean isPrimitive() {
    return primitiveTypes.find(this::equals).isPresent();
  }

  public boolean isVoid() {
    return voidType().equals(this);
  }

  public boolean isOptional() {
    return onOptional(ignore -> true).orElse(false);
  }

  /**
   * Runs the given function in case this type is an {@link Optional} with the single type parameter
   * of the optional.
   */
  public <T> Optional<T> onOptional(Function<Type, T> f) {
    final Type self = this;
    return typeParameters.headOption().filter(type -> optional(type).equals(self)).map(f);
  }

  public Optional<OptionalFieldRelation> getRelation(Type other) {
    if (this.equals(other)) {
      return Optional.of(SAME_TYPE);
    } else if (this.onOptional(other::equals).orElse(false)) {
      return Optional.of(UNWRAP_OPTIONAL);
    } else if (other.onOptional(this::equals).orElse(false)) {
      return Optional.of(WRAP_INTO_OPTIONAL);
    } else {
      return Optional.empty();
    }
  }
}
