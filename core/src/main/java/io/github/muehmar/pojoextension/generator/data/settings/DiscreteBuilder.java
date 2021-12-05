package io.github.muehmar.pojoextension.generator.data.settings;

public enum DiscreteBuilder {
  ENABLED(true),
  DISABLED(false);

  private final boolean isEnabled;

  DiscreteBuilder(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public static DiscreteBuilder fromBoolean(boolean isEnabled) {
    return isEnabled ? ENABLED : DISABLED;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public boolean isDisabled() {
    return !isEnabled();
  }
}
