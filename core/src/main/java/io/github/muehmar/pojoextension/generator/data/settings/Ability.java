package io.github.muehmar.pojoextension.generator.data.settings;

public enum Ability {
  ENABLED(true),
  DISABLED(false);

  private final boolean isEnabled;

  Ability(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public static Ability fromBoolean(boolean isEnabled) {
    return isEnabled ? ENABLED : DISABLED;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public boolean isDisabled() {
    return !isEnabled();
  }
}
