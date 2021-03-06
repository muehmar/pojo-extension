package io.github.muehmar.pojoextension.generator.model.settings;

import static io.github.muehmar.pojoextension.generator.model.settings.Ability.DISABLED;
import static io.github.muehmar.pojoextension.generator.model.settings.Ability.ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.Name;
import org.junit.jupiter.api.Test;

class PojoSettingsTest {
  @Test
  void extensionName_when_calledWithSamplePojo_then_correctExtensionName() {
    final Name name = PojoSettings.defaultSettings().extensionName(Pojos.sample());
    assertEquals("CustomerExtension", name.asString());
  }

  @Test
  void extensionName_when_calledWithInnerClassName_then_correctExtensionName() {
    final Name name =
        PojoSettings.defaultSettings()
            .extensionName(Pojos.sample().withName(Name.fromString("Customer.Address")));
    assertEquals("CustomerAddressExtension", name.asString());
  }

  @Test
  void extensionName_when_overriddenExtensionName_then_useCustomExtensionName() {
    final Name name =
        PojoSettings.defaultSettings()
            .withExtensionName(Name.fromString("MyExtension"))
            .extensionName(Pojos.sample());
    assertEquals("MyExtension", name.asString());
  }

  @Test
  void extensionName_when_overriddenExtensionNameWithClassname_then_customExtensionNameCreated() {
    final Name name =
        PojoSettings.defaultSettings()
            .withExtensionName(Name.fromString("{CLASSNAME}Ext"))
            .extensionName(Pojos.sample());
    assertEquals("CustomerExt", name.asString());
  }

  @Test
  void qualifiedExtensionName_when_calledWithSamplePojo_then_correctExtensionName() {
    final Name name = PojoSettings.defaultSettings().qualifiedExtensionName(Pojos.sample());
    assertEquals("io.github.muehmar.CustomerExtension", name.asString());
  }

  @Test
  void qualifiedExtensionName_when_calledWithInnerClassName_then_correctExtensionName() {
    final Name name =
        PojoSettings.defaultSettings()
            .qualifiedExtensionName(Pojos.sample().withName(Name.fromString("Customer.Address")));
    assertEquals("io.github.muehmar.CustomerAddressExtension", name.asString());
  }

  @Test
  void builderName_when_calledWithSamplePojo_then_correctBuilderName() {
    final Name name = PojoSettings.defaultSettings().builderName(Pojos.sample());
    assertEquals("CustomerBuilder", name.asString());
  }

  @Test
  void builderName_when_calledWithInnerClassName_then_correctBuilderName() {
    final Name name =
        PojoSettings.defaultSettings()
            .builderName(Pojos.sample().withName(Name.fromString("Customer.Address")));
    assertEquals("CustomerAddressBuilder", name.asString());
  }

  @Test
  void builderName_when_overriddenBuilderName_then_useCustomBuilderName() {
    final Name name =
        PojoSettings.defaultSettings()
            .withBuilderName(Name.fromString("MyBuilder"))
            .builderName(Pojos.sample());
    assertEquals("MyBuilder", name.asString());
  }

  @Test
  void builderName_when_overriddenBuilderNameWithClassname_then_customBuilderNameCreated() {
    final Name name =
        PojoSettings.defaultSettings()
            .withBuilderName(Name.fromString("{CLASSNAME}SafeBuilder"))
            .builderName(Pojos.sample());
    assertEquals("CustomerSafeBuilder", name.asString());
  }

  @Test
  void qualifiedBuilderName_when_calledWithSamplePojo_then_correctBuilderName() {
    final Name name = PojoSettings.defaultSettings().qualifiedBuilderName(Pojos.sample());
    assertEquals("io.github.muehmar.CustomerBuilder", name.asString());
  }

  @Test
  void qualifiedBuilderName_when_calledWithInnerClassName_then_correctBuilderName() {
    final Name name =
        PojoSettings.defaultSettings()
            .qualifiedBuilderName(Pojos.sample().withName(Name.fromString("Customer.Address")));
    assertEquals("io.github.muehmar.CustomerAddressBuilder", name.asString());
  }

  @Test
  void createExtension_when_everythingDisabled_then_false() {
    final PojoSettings settings =
        PojoSettings.defaultSettings()
            .withSafeBuilderAbility(DISABLED)
            .withMappersAbility(DISABLED)
            .withOptionalGettersAbility(DISABLED)
            .withWithersAbility(DISABLED);
    assertFalse(settings.createExtension());
  }

  @Test
  void createExtension_when_everythingExceptMappersDisabled_then_true() {
    final PojoSettings settings =
        PojoSettings.defaultSettings()
            .withSafeBuilderAbility(DISABLED)
            .withMappersAbility(ENABLED)
            .withOptionalGettersAbility(DISABLED)
            .withWithersAbility(DISABLED);
    assertTrue(settings.createExtension());
  }

  @Test
  void createExtension_when_everythingExceptOptionalGettersDisabled_then_true() {
    final PojoSettings settings =
        PojoSettings.defaultSettings()
            .withSafeBuilderAbility(DISABLED)
            .withMappersAbility(DISABLED)
            .withOptionalGettersAbility(ENABLED)
            .withWithersAbility(DISABLED);
    assertTrue(settings.createExtension());
  }

  @Test
  void createExtension_when_everythingExceptWithersDisabled_then_true() {
    final PojoSettings settings =
        PojoSettings.defaultSettings()
            .withSafeBuilderAbility(DISABLED)
            .withMappersAbility(DISABLED)
            .withOptionalGettersAbility(DISABLED)
            .withWithersAbility(ENABLED);
    assertTrue(settings.createExtension());
  }
}
