package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;

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
}
