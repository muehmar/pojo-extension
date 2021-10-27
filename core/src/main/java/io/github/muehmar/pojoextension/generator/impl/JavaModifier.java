package io.github.muehmar.pojoextension.generator.impl;

public enum JavaModifier {
  PRIVATE("private", 0),
  PROTECTED("protected", 0),
  PUBLIC("public", 0),
  STATIC("static", 1),
  ABSTRACT("abstract", 1),
  FINAL("final", 2);

  private final String value;
  private final int order;

  JavaModifier(String value, int order) {
    this.value = value;
    this.order = order;
  }

  public int getOrder() {
    return order;
  }

  public String asString() {
    return value;
  }
}
