package io.github.muehmar.pojoextension.generator.data.settings;

import static io.github.muehmar.pojoextension.generator.data.settings.Ability.ENABLED;
import static io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage.INHERITED;
import static java.util.Optional.empty;

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
  private final Ability safeBuilderAbility;
  private final Ability equalsHashCodeAbility;
  private final Ability toStringAbility;
  private final Ability withAbility;
  private final Ability mapAbility;

  PojoSettings(
      ExtensionUsage extensionUsage,
      Optional<Name> extensionName,
      Ability safeBuilderAbility,
      Ability equalsHashCodeAbility,
      Ability toStringAbility,
      Ability withAbility,
      Ability mapAbility) {
    this.extensionUsage = extensionUsage;
    this.extensionName = extensionName;
    this.safeBuilderAbility = safeBuilderAbility;
    this.equalsHashCodeAbility = equalsHashCodeAbility;
    this.toStringAbility = toStringAbility;
    this.withAbility = withAbility;
    this.mapAbility = mapAbility;
  }

  public static PojoSettings defaultSettings() {
    return newBuilder()
        .setExtensionUsage(INHERITED)
        .setSafeBuilderAbility(ENABLED)
        .setEqualsHashCodeAbility(ENABLED)
        .setToStringAbility(ENABLED)
        .setWithAbility(ENABLED)
        .setMapAbility(ENABLED)
        .andAllOptionals()
        .setExtensionName(empty())
        .build();
  }

  public ExtensionUsage getExtensionUsage() {
    return extensionUsage;
  }

  public Optional<Name> getExtensionName() {
    return extensionName;
  }

  public Ability getSafeBuilderAbility() {
    return safeBuilderAbility;
  }

  public Ability getEqualsHashCodeAbility() {
    return equalsHashCodeAbility;
  }

  public Ability getToStringAbility() {
    return toStringAbility;
  }

  public Ability getWithAbility() {
    return withAbility;
  }

  public Ability getMapAbility() {
    return mapAbility;
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
