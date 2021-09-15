package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;

public class Pojo {
  private final Name name;
  private final Name pojoName;
  private final PackageName pkg;
  private final PList<PojoMember> members;

  public Pojo(Name name, Name pojoName, PackageName pkg, PList<PojoMember> members) {
    this.name = name;
    this.pojoName = pojoName;
    this.pkg = pkg;
    this.members = members;
  }

  public Name getName() {
    return name;
  }

  public Name getPojoName() {
    return pojoName;
  }

  public PackageName getPackage() {
    return pkg;
  }

  public PList<PojoMember> getMembers() {
    return members;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pojo pojo = (Pojo) o;
    return Objects.equals(name, pojo.name)
        && Objects.equals(pojoName, pojo.pojoName)
        && Objects.equals(pkg, pojo.pkg)
        && Objects.equals(members, pojo.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pojoName, pkg, members);
  }

  @Override
  public String toString() {
    return "Pojo{"
        + "name="
        + name
        + ", pojoName="
        + pojoName
        + ", pkg="
        + pkg
        + ", members="
        + members
        + '}';
  }
}
