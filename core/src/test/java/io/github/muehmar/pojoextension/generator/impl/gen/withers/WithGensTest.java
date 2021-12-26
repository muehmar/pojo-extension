package io.github.muehmar.pojoextension.generator.impl.gen.withers;

import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.data.WithField;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class WithGensTest {

  @Test
  void withMethod_when_usedWithSampleAndField_then_correctDelegateCall() {
    final Generator<WithField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public Customer withNickname(String nickname) {\n"
            + "  return withNickname(self(), nickname);\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void withMethod_when_genericType_then_correctRefs() {
    final Generator<WithField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, PojoFields.requiredMap());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void withMethod_when_disabled_then_noOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.withMethod();
    final WithField withField = WithField.of(Pojos.sample(), PojoFields.requiredMap());
    final Writer writer =
        generator.generate(
            withField,
            PojoSettings.defaultSettings().withWithersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void withMethod_when_genericSampleAndField_then_correctDelegateCall() {
    final Generator<WithField, PojoSettings> generator = WithGens.withMethod();

    final Pojo pojo = Pojos.genericSample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public Customer<T, S> withAdditionalData(S additionalData) {\n"
            + "  return withAdditionalData(self(), additionalData);\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forRequiredFieldAndNullableArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "private static Customer withId(Customer self, Integer id) {\n"
            + "  return new Customer(id, self.getUsername(), self.getNickname().orElse(null));\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forOptionalFieldAndNullableArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "private static Customer withNickname(Customer self, String nickname) {\n"
            + "  return new Customer(self.getId(), self.getUsername(), nickname);\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forRequiredFieldAndOptionalArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "private static Customer withId(Customer self, Integer id) {\n"
            + "  return new Customer(id, self.getUsername(), self.getNickname());\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forOptionalFieldAndOptionalArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "private static Customer withNickname(Customer self, String nickname) {\n"
            + "  return new Customer(self.getId(), self.getUsername(), Optional.ofNullable(nickname));\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forFieldWithGenericType_then_correctRefs() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField = WithField.of(pojo, PojoFields.requiredMap());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void staticWithMethod_when_disabled_then_noOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();
    final WithField withField = WithField.of(Pojos.sample(), PojoFields.requiredMap());
    final Writer writer =
        generator.generate(
            withField,
            PojoSettings.defaultSettings().withWithersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void staticWithMethod_when_genericSampleAndField_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticWithMethod();

    final Pojo pojo = Pojos.genericSample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "private static <T extends List<String>, S> Customer<T, S> withAdditionalData(Customer<T, S> self, S additionalData) {\n"
            + "  return new Customer<>(self.getId(), self.getData(), additionalData);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void optionalWithMethod_when_usedWithSampleAndField_then_correctDelegateCall() {
    final Generator<WithField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public Customer withNickname(Optional<String> nickname) {\n"
            + "  return withNickname(self(), nickname);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void optionalWithMethod_when_genericType_then_correctRefs() {
    final Generator<WithField, PojoSettings> generator = WithGens.optionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField =
        WithField.of(pojo, PojoFields.requiredMap().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void optionalWithMethod_when_disabled_then_noOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.optionalWithMethod();
    final WithField withField = WithField.of(Pojos.sample(), PojoFields.requiredMap());
    final Writer writer =
        generator.generate(
            withField,
            PojoSettings.defaultSettings().withWithersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void
      staticOptionalWithMethod_when_forRequiredFieldAndNullableArguments_then_noContentGenerated() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticOptionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals("", writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticOptionalWithMethod_when_forOptionalFieldAndNullableArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticOptionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withNickname(Customer self, Optional<String> nickname) {\n"
            + "  return new Customer(self.getId(), self.getUsername(), nickname.orElse(null));\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticOptionalWithMethod_when_forOptionalFieldAndOptionalArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticOptionalWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withNickname(Customer self, Optional<String> nickname) {\n"
            + "  return new Customer(self.getId(), self.getUsername(), nickname);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticOptionalWithMethod_when_forFieldWithGenericType_then_correctRefs() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticOptionalWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField =
        WithField.of(pojo, PojoFields.requiredMap().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
  }

  @Test
  void staticOptionalWithMethod_when_disabled_then_noOutput() {
    final Generator<WithField, PojoSettings> generator = WithGens.staticOptionalWithMethod();
    final WithField withField = WithField.of(Pojos.sample(), PojoFields.requiredMap());
    final Writer writer =
        generator.generate(
            withField,
            PojoSettings.defaultSettings().withWithersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }
}
