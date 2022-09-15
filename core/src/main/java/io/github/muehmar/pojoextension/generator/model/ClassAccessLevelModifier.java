package io.github.muehmar.pojoextension.generator.model;

import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;

public enum ClassAccessLevelModifier {
  PUBLIC(Optional.of(JavaModifier.PUBLIC)),
  PACKAGE_PRIVATE(Optional.empty());

  private final Optional<JavaModifier> javaModifier;

  ClassAccessLevelModifier(Optional<JavaModifier> javaModifier) {
    this.javaModifier = javaModifier;
  }

  public Optional<JavaModifier> asJavaModifier() {
    return javaModifier;
  }
}
