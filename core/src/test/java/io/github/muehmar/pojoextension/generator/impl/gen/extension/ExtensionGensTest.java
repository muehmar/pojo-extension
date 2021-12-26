package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.Resources;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.DiscreteBuilder;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ExtensionGensTest {
  @Test
  void extensionClass_when_generatorUsedWithSamplePojo_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionClass();

    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(readResourcePojoTemplate("SamplePojo"), output);
  }

  @Test
  void extensionClass_when_withNonDiscreteBuilder_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionClass();

    final String output =
        generator
            .generate(
                Pojos.sample(),
                PojoSettings.defaultSettings().withDiscreteBuilder(DiscreteBuilder.DISABLED),
                Writer.createDefault())
            .asString();

    assertEquals(readResourcePojoTemplate("SamplePojoIncludingSafeBuilder"), output);
  }

  @Test
  void extensionClass_when_everythingDisabled_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionClass();

    final String output =
        generator
            .generate(
                Pojos.sample(),
                PojoSettings.defaultSettings()
                    .withEqualsHashCodeAbility(DISABLED)
                    .withToStringAbility(DISABLED)
                    .withSafeBuilderAbility(DISABLED)
                    .withWithersAbility(DISABLED)
                    .withMappersAbility(DISABLED),
                Writer.createDefault())
            .asString();

    assertEquals(readResourcePojoTemplate("AllDisabledSamplePojo"), output);
  }

  @Test
  void constructor_when_sample_then_correctContent() {
    final Writer writer =
        ExtensionGens.constructor()
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "protected CustomerExtension() {\n"
            + "  final Object o = this;\n"
            + "  if(!(o instanceof Customer))\n"
            + "    throw new IllegalArgumentException(\"Only class Customer can extend CustomerExtension.\");\n"
            + "}",
        writer.asString());
  }

  @Test
  void constructor_when_genericSample_then_correctContent() {
    final Writer writer =
        ExtensionGens.constructor()
            .generate(
                Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "protected CustomerExtension() {\n"
            + "  final Object o = this;\n"
            + "  if(!(o instanceof Customer<?, ?>))\n"
            + "    throw new IllegalArgumentException(\"Only class Customer can extend CustomerExtension.\");\n"
            + "}",
        writer.asString());
  }

  @Test
  void selfMethod_when_sample_then_correctContent() {
    final Writer writer =
        ExtensionGens.selfMethod()
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private Customer self() {\n"
            + "  final Object self = this;\n"
            + "  return (Customer)self;\n"
            + "}",
        writer.asString());
  }

  @Test
  void selfMethod_when_genericSample_then_correctContent() {
    final Writer writer =
        ExtensionGens.selfMethod()
            .generate(
                Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private Customer self() {\n"
            + "  final Object self = this;\n"
            + "  return (Customer<T, S>)self;\n"
            + "}",
        writer.asString());
  }

  private static String readResourcePojoTemplate(String template) {
    return Resources.readString("/java/pojos/" + template + ".java.template");
  }
}
