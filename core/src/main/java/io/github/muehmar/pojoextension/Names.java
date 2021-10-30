package io.github.muehmar.pojoextension;

import io.github.muehmar.pojoextension.generator.data.Name;

public class Names {
  private Names() {}

  public static Name extensionSuffix() {
    return Name.fromString("Extension");
  }
}
