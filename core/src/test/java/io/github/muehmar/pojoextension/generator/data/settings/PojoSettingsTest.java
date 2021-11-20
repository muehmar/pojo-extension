package io.github.muehmar.pojoextension.generator.data.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
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
  void getStaticMethodAccessModifier_when_extensionUsageIsStatic_then_accessModifierIsPublic() {
    final PojoSettings settings =
        PojoSettings.defaultSettings().withExtensionUsage(ExtensionUsage.STATIC);
    assertEquals(JavaModifier.PUBLIC, settings.getStaticMethodAccessModifier());
  }

  @Test
  void getStaticMethodAccessModifier_when_extensionUsageIsInherited_then_accessModifierIsPrivate() {
    final PojoSettings settings =
        PojoSettings.defaultSettings().withExtensionUsage(ExtensionUsage.INHERITED);
    assertEquals(JavaModifier.PRIVATE, settings.getStaticMethodAccessModifier());
  }
}
