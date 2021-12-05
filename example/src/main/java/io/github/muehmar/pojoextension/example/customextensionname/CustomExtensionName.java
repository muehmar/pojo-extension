package io.github.muehmar.pojoextension.example.customextensionname;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension(extensionName = "{CLASSNAME}Class")
@SuppressWarnings("java:S2160") // Not overriding equals and hashCode is fine
public class CustomExtensionName extends CustomExtensionNameClass {
  private final String name;

  public CustomExtensionName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
