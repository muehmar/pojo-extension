package io.github.muehmar.pojoextension.generator.data;

public enum Necessity {
  REQUIRED(true),
  OPTIONAL(false);

  private final boolean required;

  Necessity(boolean required) {
    this.required = required;
  }

  public boolean isRequired() {
    return required;
  }

  public boolean isOptional() {
    return !isRequired();
  }
}
