package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.PojoExtension.Builder0;
import java.util.Objects;

@PojoExtension
public class Pojo {
  private final Name extensionName;
  private final Name pojoName;
  private final PackageName pkg;
  private final PList<PojoField> fields;
  private final PList<Constructor> constructors;

  public Pojo(
      Name extensionName,
      Name pojoName,
      PackageName pkg,
      PList<PojoField> fields,
      PList<Constructor> constructors) {
    this.extensionName = extensionName;
    this.pojoName = pojoName;
    this.pkg = pkg;
    this.fields = fields;
    this.constructors = constructors;
  }

  public Name getExtensionName() {
    return extensionName;
  }

  public Name getPojoName() {
    return pojoName;
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

  public Pojo withConstructors(PList<Constructor> constructors) {
    return new Pojo(extensionName, pojoName, pkg, fields, constructors);
  }

  public static Builder0 newBuilder() {
    return io.github.muehmar.pojoextension.generator.data.PojoExtension.newBuilder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pojo pojo = (Pojo) o;
    return Objects.equals(extensionName, pojo.extensionName)
        && Objects.equals(pojoName, pojo.pojoName)
        && Objects.equals(pkg, pojo.pkg)
        && Objects.equals(fields, pojo.fields)
        && Objects.equals(constructors, pojo.constructors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extensionName, pojoName, pkg, fields, constructors);
  }

  @Override
  public String toString() {
    return "Pojo{"
        + "extensionName="
        + extensionName
        + ", pojoName="
        + pojoName
        + ", pkg="
        + pkg
        + ", fields="
        + fields
        + ", constructors="
        + constructors
        + '}';
  }
}
