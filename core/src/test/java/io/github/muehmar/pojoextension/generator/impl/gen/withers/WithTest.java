package io.github.muehmar.pojoextension.generator.impl.gen.withers;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.data.WithField;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class WithTest {

  @Test
  void withMethod_when_usedWithSampleAndField_then_correctDelegateCall() {
    final Generator<WithField, PojoSettings> generator = With.withMethod();

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
  void staticWithMethod_when_forRequiredFieldAndNullableArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = With.staticWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withId(Customer self, Integer id) {\n"
            + "  return new Customer(id, self.getUsername(), self.getNickname().orElse(null));\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forOptionalFieldAndNullableArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = With.staticWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withNickname(Customer self, String nickname) {\n"
            + "  return new Customer(self.getId(), self.getUsername(), nickname);\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forRequiredFieldAndOptionalArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = With.staticWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withId(Customer self, Integer id) {\n"
            + "  return new Customer(id, self.getUsername(), self.getNickname());\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticWithMethod_when_forOptionalFieldAndOptionalArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = With.staticWithMethod();

    final Pojo pojo = Pojos.sampleWithConstructorWithOptionalArgument();
    final WithField withField = WithField.of(pojo, pojo.getFields().apply(2));

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withNickname(Customer self, String nickname) {\n"
            + "  return new Customer(self.getId(), self.getUsername(), Optional.ofNullable(nickname));\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void optionalWithMethod_when_usedWithSampleAndField_then_correctDelegateCall() {
    final Generator<WithField, PojoSettings> generator = With.optionalWithMethod();

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
  }

  @Test
  void
      staticOptionalWithMethod_when_forRequiredFieldAndNullableArguments_then_noContentGenerated() {
    final Generator<WithField, PojoSettings> generator = With.staticOptionalWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals("", writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void staticOptionalWithMethod_when_forOptionalFieldAndNullableArguments_then_correctOutput() {
    final Generator<WithField, PojoSettings> generator = With.staticOptionalWithMethod();

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
    final Generator<WithField, PojoSettings> generator = With.staticOptionalWithMethod();

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
}
