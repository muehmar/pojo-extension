package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.Resources;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
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
  void getterMethod_when_samplePojo_then_correctContent() {
    final Pojo pojo = Pojos.sample();
    final FieldGetter fieldGetter =
        FieldGetter.of(pojo.getGetters().head(), pojo.getFields().head(), SAME_TYPE);
    final Writer writer =
        ExtensionGens.getterMethod()
            .generate(fieldGetter, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("Integer getId();", writer.asString());
  }

  @Test
  void getterMethod_when_genenricPojo_then_correctContent() {
    final Pojo pojo = Pojos.genericSample();
    final FieldGetter fieldGetter =
        FieldGetter.of(
            pojo.getGetters().drop(1).head(), pojo.getFields().drop(1).head(), SAME_TYPE);
    final Writer writer =
        ExtensionGens.getterMethod()
            .generate(fieldGetter, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("T getData();", writer.asString());
  }

  private static String readResourcePojoTemplate(String template) {
    return Resources.readString("/java/pojos/" + template + ".java.template");
  }
}
