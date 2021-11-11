package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoSettings extends PojoSettingsExtension {
  private final boolean disableSafeBuilder;
  private final Optional<Name> extensionName;

  public PojoSettings(boolean disableSafeBuilder, Optional<Name> extensionName) {
    this.disableSafeBuilder = disableSafeBuilder;
    this.extensionName = extensionName;
  }

  public static PojoSettings defaultSettings() {
    return new PojoSettings(false, Optional.empty());
  }

  public boolean isDisableSafeBuilder() {
    return disableSafeBuilder;
  }

  public boolean isEnableSafeBuilder() {
    return !isDisableSafeBuilder();
  }

  public Name qualifiedExtensionName(Pojo pojo) {
    return pojo.getPkg().qualifiedName(extensionName(pojo));
  }

  public Optional<Name> getExtensionName() {
    return extensionName;
  }

  public Name extensionName(Pojo pojo) {
    return extensionName.orElseGet(
        () -> pojo.getName().map(n -> n.replace(".", "")).append("Extension"));
  }
}
