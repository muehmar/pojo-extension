package io.github.muehmar.pojoextension.example.customextensionname;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension(extensionName = "{CLASSNAME}Class")
@SuppressWarnings("java:S2160") // Not overriding equals and hashCode is fine
public class CustomExtensionName implements CustomExtensionNameClass {
  private final String name;

  public CustomExtensionName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    return genEquals(o);
  }

  @Override
  public int hashCode() {
    return genHashCode();
  }

  @Override
  public String toString() {
    return genToString();
  }
}
