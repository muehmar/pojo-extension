package io.github.muehmar.pojoextension.generator.model;

import lombok.Value;

@Value
public class PackageName {
  String pkg;

  private PackageName(String pkg) {
    this.pkg = pkg;
  }

  public static PackageName fromString(String pkg) {
    return new PackageName(pkg);
  }

  public static PackageName javaLang() {
    return fromString("java.lang");
  }

  public static PackageName javaUtil() {
    return fromString("java.util");
  }

  public Name qualifiedName(Name className) {
    return className.prefix(".").prefix(pkg);
  }

  public String asString() {
    return pkg;
  }

  @Override
  public String toString() {
    return asString();
  }
}
