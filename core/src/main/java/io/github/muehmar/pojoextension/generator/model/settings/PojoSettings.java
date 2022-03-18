package io.github.muehmar.pojoextension.generator.model.settings;

import static io.github.muehmar.pojoextension.generator.model.settings.ExtensionUsage.INHERITED;
import static java.util.Optional.empty;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class PojoSettings implements PojoSettingsExtension {
  private static final Name CLASS_NAME_PLACEHOLDER = Name.fromString("{CLASSNAME}");
  public static final Name BUILDER_CLASS_POSTFIX = Name.fromString("Builder");
  public static final Name EXTENSION_IFC_POSTFIX = Name.fromString("Extension");
  PList<OptionalDetection> optionalDetections;
  ExtensionUsage extensionUsage;
  Optional<Name> extensionName;
  Optional<Name> builderName;
  Optional<Name> builderSetMethodPrefix;
  Ability safeBuilderAbility;
  Ability withersAbility;
  Ability optionalGettersAbility;
  Ability mappersAbility;

  public static PojoSettings defaultSettings() {
    return PojoSettingsBuilder.create()
        .optionalDetections(
            PList.of(OptionalDetection.OPTIONAL_CLASS, OptionalDetection.NULLABLE_ANNOTATION))
        .extensionUsage(INHERITED)
        .safeBuilderAbility(Ability.ENABLED)
        .withersAbility(Ability.ENABLED)
        .optionalGettersAbility(Ability.ENABLED)
        .mappersAbility(Ability.ENABLED)
        .andAllOptionals()
        .extensionName(Optional.of(CLASS_NAME_PLACEHOLDER.append(EXTENSION_IFC_POSTFIX)))
        .builderName(Optional.of(CLASS_NAME_PLACEHOLDER.append(BUILDER_CLASS_POSTFIX)))
        .builderSetMethodPrefix(empty())
        .build();
  }

  public Name qualifiedExtensionName(Pojo pojo) {
    return pojo.getPackage().qualifiedName(extensionName(pojo));
  }

  public Name extensionName(Pojo pojo) {
    return getNameOrAppend(extensionName, EXTENSION_IFC_POSTFIX, pojo);
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
    return withersAbility.isEnabled()
        || optionalGettersAbility.isEnabled()
        || mappersAbility.isEnabled();
  }
}
