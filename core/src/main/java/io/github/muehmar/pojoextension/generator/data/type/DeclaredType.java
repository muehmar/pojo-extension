package io.github.muehmar.pojoextension.generator.data.type;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class DeclaredType extends DeclaredTypeBase implements SpecificType {
  private final Name name;
  private final Optional<PackageName> pkg;
  private final PList<Type> typeParameters;

  DeclaredType(Name name, Optional<PackageName> pkg, PList<Type> typeParameters) {
    this.name = name;
    this.pkg = pkg;
    this.typeParameters = typeParameters;
  }

  public static DeclaredType fromNameAndPackage(Name name, PackageName packageName) {
    return new DeclaredType(name, Optional.of(packageName), PList.empty());
  }

  public static DeclaredType of(Name name, PackageName packageName, Type singeTypeParameter) {
    return new DeclaredType(name, Optional.of(packageName), PList.single(singeTypeParameter));
  }

  public static DeclaredType of(Name name, PackageName packageName, PList<Type> typeParameters) {
    return new DeclaredType(name, Optional.of(packageName), typeParameters);
  }

  public static DeclaredType of(
      Name name, Optional<PackageName> packageName, PList<Type> typeParameters) {
    return new DeclaredType(name, packageName, typeParameters);
  }

  public static DeclaredType optional(Type value) {
    return of(Name.fromString("Optional"), PackageName.javaUtil(), value);
  }

  @Override
  public TypeKind getKind() {
    return TypeKind.DECLARED;
  }

  @Override
  public Name getTypeDeclaration() {
    final Optional<Name> formattedTypeParameters =
        typeParameters
            .map(Type::getTypeDeclaration)
            .reduce((s1, s2) -> s1.append(",").append(s2))
            .map(s -> s.prefix("<").append(">"));
    return name.append(formattedTypeParameters.map(Name::asString).orElse(""));
  }

  @Override
  public PList<Name> getImports() {
    return PList.fromOptional(pkg.map(p -> name.prefix(p + ".")))
        .concat(typeParameters.flatMap(Type::getImports));
  }

  @Override
  public Name getName() {
    return name;
  }

  public Optional<PackageName> getPkg() {
    return pkg;
  }

  public PList<Type> getTypeParameters() {
    return typeParameters;
  }

  public boolean isOptional() {
    return typeParameters.size() == 1 && this.equals(optional(typeParameters.head()));
  }
}
