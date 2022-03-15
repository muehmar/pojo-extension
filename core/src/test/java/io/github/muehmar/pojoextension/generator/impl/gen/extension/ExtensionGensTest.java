package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.TemplateTestUtil.assertTemplateEqualsOrUpdate;
import static io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.model.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.FieldGetter;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ExtensionGensTest {
  @Test
  void extensionInterface_when_generatorUsedWithSamplePojo_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionInterface();

    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertTemplateEqualsOrUpdate("SamplePojo", output);
  }

  @Test
  void extensionInterface_when_generatorUsedWithGenericPojo_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionInterface();

    final String output =
        generator
            .generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertTemplateEqualsOrUpdate("GenericSamplePojo", output);
  }

  @Test
  void extensionInterface_when_everythingDisabled_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionInterface();

    final String output =
        generator
            .generate(
                Pojos.sample(),
                PojoSettings.defaultSettings()
                    .withEqualsHashCodeAbility(DISABLED)
                    .withToStringAbility(DISABLED)
                    .withSafeBuilderAbility(DISABLED)
                    .withWithersAbility(DISABLED)
                    .withOptionalGettersAbility(DISABLED)
                    .withMappersAbility(DISABLED),
                Writer.createDefault())
            .asString();

    assertTemplateEqualsOrUpdate("AllDisabledSamplePojo", output);
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
  void getterMethod_when_samplePojoForOptionalField_then_correctContent() {
    final Pojo pojo = Pojos.sample();
    final FieldGetter fieldGetter =
        FieldGetter.of(
            pojo.getGetters().drop(2).head(), pojo.getFields().drop(2).head(), SAME_TYPE);
    final Writer writer =
        ExtensionGens.getterMethod()
            .generate(fieldGetter, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("Optional<String> getNickname();", writer.asString());
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
}
