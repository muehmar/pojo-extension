package io.github.muehmar.pojoextension.generator.data.settings;

import static io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage.INHERITED;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoSettings extends PojoSettingsExtension {
  private final ExtensionUsage extensionUsage;
  private final Optional<Name> extensionName;

  PojoSettings(ExtensionUsage extensionUsage, Optional<Name> extensionName) {
    this.extensionUsage = extensionUsage;
    this.extensionName = extensionName;
  }

  public static PojoSettings defaultSettings() {
    return new PojoSettings(INHERITED, Optional.empty());
  }

  public ExtensionUsage getExtensionUsage() {
    return extensionUsage;
  }

  public Optional<Name> getExtensionName() {
    return extensionName;
  }

  public Name qualifiedExtensionName(Pojo pojo) {
    return pojo.getPackage().qualifiedName(extensionName(pojo));
  }

  public Name extensionName(Pojo pojo) {
    return extensionName.orElseGet(
        () -> pojo.getName().map(n -> n.replace(".", "")).append("Extension"));
  }

  public JavaModifier getStaticMethodAccessModifier() {
    return extensionUsage.isStatic() ? JavaModifier.PUBLIC : JavaModifier.PRIVATE;
  }
}
