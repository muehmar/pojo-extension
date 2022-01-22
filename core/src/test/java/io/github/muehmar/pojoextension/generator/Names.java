package io.github.muehmar.pojoextension.generator;

import io.github.muehmar.pojoextension.generator.data.Name;

public class Names {
  private Names() {}

  public static Name id() {
    return Name.fromString("id");
  }

  public static Name zip() {
    return Name.fromString("zip");
  }
}
