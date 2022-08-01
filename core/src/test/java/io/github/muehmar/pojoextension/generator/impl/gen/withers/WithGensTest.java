package io.github.muehmar.pojoextension.generator.impl.gen.withers;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoAndField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import org.junit.jupiter.api.Test;

class WithGensTest {
  @Test
  void withMethod_when_genericType_then_correctRefs() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, PojoFields.requiredMap());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void withMethod_when_disabled_then_noOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();
    final PojoAndField pojoAndField = PojoAndField.of(Pojos.sample(), PojoFields.requiredMap());
    final Writer writer =
        generator.generate(
            pojoAndField,
            PojoSettings.defaultSettings().withWithersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void withMethod_when_forRequiredFieldAndNullableArguments_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer withId(Integer id) {\n"
            + "  return new Customer(id, getUsername(), getNickname().orElse(null));\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void withMethod_when_forOptionalFieldAndNullableArguments_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer withNickname(String nickname) {\n"
            + "  return new Customer(getId(), getUsername(), nickname);\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void withMethod_when_forRequiredFieldAndOptionalArguments_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer withId(Integer id) {\n"
            + "  return new Customer(id, getUsername(), getNickname());\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void withMethod_when_forOptionalFieldAndOptionalArguments_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer withNickname(String nickname) {\n"
            + "  return new Customer(getId(), getUsername(), Optional.ofNullable(nickname));\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void withMethod_when_forFieldWithGenericType_then_correctRefs() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, PojoFields.requiredMap());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void withMethod_when_genericSampleAndField_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.genericSample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer<T, S> withAdditionalData(S additionalData) {\n"
            + "  return new Customer<>(getId(), getData(), additionalData);\n"
            + "}",
        writer.asString());
  }

  @Test
  void optionalWithMethod_when_genericType_then_correctRefs() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final PojoAndField pojoAndField =
        PojoAndField.of(pojo, PojoFields.requiredMap().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void optionalWithMethod_when_forRequiredFieldAndNullableArguments_then_noContentGenerated() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals("", writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void optionalWithMethod_when_forOptionalFieldAndNullableArguments_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer withNickname(Optional<String> nickname) {\n"
            + "  return new Customer(getId(), getUsername(), nickname.orElse(null));\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void optionalWithMethod_when_forOptionalFieldAndOptionalArguments_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer withNickname(Optional<String> nickname) {\n"
            + "  return new Customer(getId(), getUsername(), nickname);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void optionalWithMethod_when_forFieldWithGenericType_then_correctRefs() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final PojoAndField pojoAndField =
        PojoAndField.of(pojo, PojoFields.requiredMap().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void optionalWithMethod_when_disabled_then_noOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();
    final PojoAndField pojoAndField = PojoAndField.of(Pojos.sample(), PojoFields.requiredMap());
    final Writer writer =
        generator.generate(
            pojoAndField,
            PojoSettings.defaultSettings().withWithersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void optionalWithMethod_when_genericSample_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.genericSample();
    final PojoAndField pojoAndField = PojoAndField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default Customer<T, S> withAdditionalData(Optional<S> additionalData) {\n"
            + "  return new Customer<>(getId(), getData(), additionalData.orElse(null));\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }
}
