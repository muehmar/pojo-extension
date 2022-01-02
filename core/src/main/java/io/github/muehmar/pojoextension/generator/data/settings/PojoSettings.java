package io.github.muehmar.pojoextension.generator.data.settings;

import static io.github.muehmar.pojoextension.generator.data.settings.Ability.ENABLED;
import static io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage.INHERITED;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoSettings extends PojoSettingsExtension {
  private static final Name CLASS_NAME_PLACEHOLDER = Name.fromString("{CLASSNAME}");
  public static final Name BUILDER_CLASS_POSTFIX = Name.fromString("Builder");
  public static final Name EXTENSION_CLASS_POSTFIX = Name.fromString("Extension");
  private final PList<OptionalDetection> optionalDetections;
  private final ExtensionUsage extensionUsage;
  private final Optional<Name> extensionName;
  private final Optional<Name> builderName;
  private final Ability safeBuilderAbility;
  private final DiscreteBuilder discreteBuilder;
  private final Ability equalsHashCodeAbility;
  private final Ability toStringAbility;
  private final Ability withersAbility;
  private final Ability mappersAbility;

  PojoSettings(
      PList<OptionalDetection> optionalDetections,
      ExtensionUsage extensionUsage,
      Optional<Name> extensionName,
      Optional<Name> builderName,
      Ability safeBuilderAbility,
      DiscreteBuilder discreteBuilder,
      Ability equalsHashCodeAbility,
      Ability toStringAbility,
      Ability withAbility,
      Ability mapAbility) {
    this.optionalDetections = optionalDetections;
    this.extensionUsage = extensionUsage;
    this.extensionName = extensionName;
    this.builderName = builderName;
    this.safeBuilderAbility = safeBuilderAbility;
    this.discreteBuilder = discreteBuilder;
    this.equalsHashCodeAbility = equalsHashCodeAbility;
    this.toStringAbility = toStringAbility;
    this.withersAbility = withAbility;
    this.mappersAbility = mapAbility;
  }

  public static PojoSettings defaultSettings() {
    return PojoSettingsBuilder.create()
        .setOptionalDetections(
            PList.of(OptionalDetection.OPTIONAL_CLASS, OptionalDetection.NULLABLE_ANNOTATION))
        .setExtensionUsage(INHERITED)
        .setSafeBuilderAbility(ENABLED)
        .setDiscreteBuilder(DiscreteBuilder.ENABLED)
        .setEqualsHashCodeAbility(ENABLED)
        .setToStringAbility(ENABLED)
        .setWithersAbility(ENABLED)
        .setMappersAbility(ENABLED)
        .andAllOptionals()
        .setExtensionName(Optional.of(CLASS_NAME_PLACEHOLDER.append(EXTENSION_CLASS_POSTFIX)))
        .setBuilderName(Optional.of(CLASS_NAME_PLACEHOLDER.append(BUILDER_CLASS_POSTFIX)))
        .build();
  }

  public PList<OptionalDetection> getOptionalDetections() {
    return optionalDetections;
  }

  public ExtensionUsage getExtensionUsage() {
    return extensionUsage;
  }

  public Optional<Name> getExtensionName() {
    return extensionName;
  }

  public Optional<Name> getBuilderName() {
    return builderName;
  }

  public Ability getSafeBuilderAbility() {
    return safeBuilderAbility;
  }

  public DiscreteBuilder getDiscreteBuilder() {
    return discreteBuilder;
  }

  public Ability getEqualsHashCodeAbility() {
    return equalsHashCodeAbility;
  }

  public Ability getToStringAbility() {
    return toStringAbility;
  }

  public Ability getWithersAbility() {
    return withersAbility;
  }

  public Ability getMappersAbility() {
    return mappersAbility;
  }

  public Name qualifiedExtensionName(Pojo pojo) {
    return pojo.getPackage().qualifiedName(extensionName(pojo));
  }

  public Name extensionName(Pojo pojo) {
    return getNameOrAppend(extensionName, EXTENSION_CLASS_POSTFIX, pojo);
  }

  public Name qualifiedBuilderName(Pojo pojo) {
    return pojo.getPackage().qualifiedName(builderName(pojo));
  }

  public Name builderName(Pojo pojo) {
    return getNameOrAppend(builderName, BUILDER_CLASS_POSTFIX, pojo);
  }

  private Name getNameOrAppend(Optional<Name> name, Name postfix, Pojo pojo) {
    return name.map(n -> n.replace(CLASS_NAME_PLACEHOLDER, getClassName(pojo)))
        .orElseGet(() -> getClassName(pojo).append(postfix));
  }

  private Name getClassName(Pojo pojo) {
    return pojo.getName().map(n -> n.replace(".", ""));
  }

  public boolean createDiscreteBuilder() {
    return safeBuilderAbility.isEnabled();
  }

  public boolean createExtension() {
    return equalsHashCodeAbility.isEnabled()
        || toStringAbility.isEnabled()
        || withersAbility.isEnabled()
        || mappersAbility.isEnabled();
  }
}
