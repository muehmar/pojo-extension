package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoSettings extends PojoSettingsExtension {
  private final Optional<Name> extensionName;

  PojoSettings(Optional<Name> extensionName) {
    this.extensionName = extensionName;
  }

  public static PojoSettings defaultSettings() {
    return new PojoSettings(Optional.empty());
  }

  public Name qualifiedExtensionName(Pojo pojo) {
    return pojo.getPackage().qualifiedName(extensionName(pojo));
  }

  public Optional<Name> getExtensionName() {
    return extensionName;
  }

  public Name extensionName(Pojo pojo) {
    return extensionName.orElseGet(
        () -> pojo.getName().map(n -> n.replace(".", "")).append("Extension"));
  }
}
