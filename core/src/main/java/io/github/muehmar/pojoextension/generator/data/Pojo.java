package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.PojoExtension.Builder0;
import java.util.Objects;
import java.util.Optional;

@PojoExtension
public class Pojo {
  private final Name name;
  private final PackageName pkg;
  private final PList<PojoField> fields;
  private final PList<Constructor> constructors;

  public Pojo(
      Name name, PackageName pkg, PList<PojoField> fields, PList<Constructor> constructors) {
    this.name = name;
    this.pkg = pkg;
    this.fields = fields;
    this.constructors = constructors;
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

  public Optional<MatchingConstructor> findMatchingConstructor() {
    return constructors
        .flatMapOptional(c -> c.matchFields(fields).map(f -> new MatchingConstructor(c, f)))
        .headOption();
  }

  public Pojo withFields(PList<PojoField> fields) {
    return new Pojo(name, pkg, fields, constructors);
  }

  public Pojo withConstructors(PList<Constructor> constructors) {
    return new Pojo(name, pkg, fields, constructors);
  }

  public static Builder0 newBuilder() {
    return io.github.muehmar.pojoextension.generator.data.PojoExtension.newBuilder();
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
