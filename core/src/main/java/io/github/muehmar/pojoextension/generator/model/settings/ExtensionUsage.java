package io.github.muehmar.pojoextension.generator.model.settings;

public enum ExtensionUsage {
  INHERITED(true),
  STATIC(false);

  private final boolean isInherited;

  ExtensionUsage(boolean isInherited) {
    this.isInherited = isInherited;
  }

  public boolean isInherited() {
    return isInherited;
  }

  public boolean isStatic() {
    return !isInherited();
  }
}
