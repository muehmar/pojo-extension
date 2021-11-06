package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;
import java.util.Optional;

@PojoExtension
public class Pojo extends io.github.muehmar.pojoextension.generator.data.PojoExtension {
  private final Name name;
  private final PackageName pkg;
  private final PList<PojoField> fields;
  private final PList<Constructor> constructors;
  private final PList<Getter> getters;

  public Pojo(
      Name name,
      PackageName pkg,
      PList<PojoField> fields,
      PList<Constructor> constructors,
      PList<Getter> getters) {
    this.name = name;
    this.pkg = pkg;
    this.fields = fields;
    this.constructors = constructors;
    this.getters = getters;
  }

  public Name getName() {
    return name;
  }

  public Name qualifiedName() {
    return pkg.qualifiedName(name);
  }

  public PackageName getPackage() {
    return pkg;
  }

  public PList<PojoField> getFields() {
    return fields;
  }

  public PList<Constructor> getConstructors() {
    return constructors;
  }

  public PList<Getter> getGetters() {
    return getters;
  }

  public Optional<MatchingConstructor> findMatchingConstructor() {
    return constructors
        .flatMapOptional(c -> c.matchFields(fields).map(f -> new MatchingConstructor(c, f)))
        .headOption();
  }

  public Pojo withFields(PList<PojoField> fields) {
    return new Pojo(name, pkg, fields, constructors, getters);
  }

  public Pojo withConstructors(PList<Constructor> constructors) {
    return new Pojo(name, pkg, fields, constructors, getters);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pojo pojo = (Pojo) o;
    return Objects.equals(name, pojo.name)
        && Objects.equals(pkg, pojo.pkg)
        && Objects.equals(fields, pojo.fields)
        && Objects.equals(constructors, pojo.constructors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pkg, fields, constructors);
  }

  @Override
  public String toString() {
    return "Pojo{"
        + "name="
        + name
        + ", pkg="
        + pkg
        + ", fields="
        + fields
        + ", constructors="
        + constructors
        + '}';
  }
}
