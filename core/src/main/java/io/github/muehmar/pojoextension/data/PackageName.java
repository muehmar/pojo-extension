package io.github.muehmar.pojoextension.data;

import java.util.Objects;

public class PackageName {
  private final String pkg;

  private PackageName(String pkg) {
    this.pkg = pkg;
  }

  public static PackageName fromString(String pkg) {
    return new PackageName(pkg);
  }

  public String asString() {
    return pkg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PackageName packageName = (PackageName) o;
    return Objects.equals(pkg, packageName.pkg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pkg);
  }

  @Override
  public String toString() {
    return "Package{" + "pkg='" + pkg + '\'' + '}';
  }
}
