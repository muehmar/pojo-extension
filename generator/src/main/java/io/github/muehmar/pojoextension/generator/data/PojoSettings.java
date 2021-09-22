package io.github.muehmar.pojoextension.generator.data;

import java.util.Objects;

public class PojoSettings {
  private final boolean disableSafeBuilder;

  public PojoSettings(boolean disableSafeBuilder) {
    this.disableSafeBuilder = disableSafeBuilder;
  }

  public boolean isDisableSafeBuilder() {
    return disableSafeBuilder;
  }

  public boolean isEnableSafeBuilder() {
    return !isDisableSafeBuilder();
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PojoSettings that = (PojoSettings) o;
    return disableSafeBuilder == that.disableSafeBuilder;
  }

  @Override
  public int hashCode() {
    return Objects.hash(disableSafeBuilder);
  }
}
