package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension(extensionName = "MyExtension")
@SuppressWarnings("java:S2160") // Not overriding equals and hashCode is fine
public class CustomExtensionName extends MyExtension {
  private final String name;

  public CustomExtensionName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
