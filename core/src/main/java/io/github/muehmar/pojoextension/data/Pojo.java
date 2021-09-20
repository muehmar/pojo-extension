package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;

public class Pojo {
  private final Name extensionName;
  private final Name pojoName;
  private final PackageName pkg;
  private final PList<PojoField> fields;

  public Pojo(Name extensionName, Name pojoName, PackageName pkg, PList<PojoField> fields) {
    this.extensionName = extensionName;
    this.pojoName = pojoName;
    this.pkg = pkg;
    this.fields = fields;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pojo pojo = (Pojo) o;
    return Objects.equals(extensionName, pojo.extensionName)
        && Objects.equals(pojoName, pojo.pojoName)
        && Objects.equals(pkg, pojo.pkg)
        && Objects.equals(fields, pojo.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extensionName, pojoName, pkg, fields);
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
        + '}';
  }
}
