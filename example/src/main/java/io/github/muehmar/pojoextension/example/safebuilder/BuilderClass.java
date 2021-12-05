package io.github.muehmar.pojoextension.example.safebuilder;

import io.github.muehmar.pojoextension.annotations.SafeBuilder;

@SafeBuilder(builderName = "SafeBuilder")
public class BuilderClass {
  private final String prop1;
  private final String prop2;

  public BuilderClass(String prop1, String prop2) {
    this.prop1 = prop1;
    this.prop2 = prop2;
  }

  public String getProp1() {
    return prop1;
  }

  public String getProp2() {
    return prop2;
  }
}
