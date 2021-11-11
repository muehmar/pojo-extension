package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.SafeBuilderPojoField;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class SafeBuilderGensTest {

  @Test
  void fieldBuilderClass_when_generatorUsedWithRequiredField_then_correctOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator =
        SafeBuilderGens.fieldBuilderClass();
    final SafeBuilderPojoField field = new SafeBuilderPojoField(PojoFields.requiredId(), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static final class Builder2 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private Builder2(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder3 setId(Integer id) {\n"
            + "    return new Builder3(builder.setId(id));\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void fieldBuilderClass_when_generatorUsedWithOptionalField_then_correctOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator =
        SafeBuilderGens.fieldBuilderClass();
    final SafeBuilderPojoField field =
        new SafeBuilderPojoField(PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static final class OptBuilder2 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private OptBuilder2(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder3 setId(Integer id) {\n"
            + "    return new OptBuilder3(builder.setId(id));\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder3 setId(Optional<Integer> id) {\n"
            + "    return new OptBuilder3(id.map(builder::setId).orElse(builder));\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void builderClassContent_when_generatorUsedWithRequiredField_then_correctOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator =
        SafeBuilderGens.builderClassContent();
    final SafeBuilderPojoField field = new SafeBuilderPojoField(PojoFields.requiredId(), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private final Builder builder;\n"
            + "\n"
            + "private Builder2(Builder builder) {\n"
            + "  this.builder = builder;\n"
            + "}\n"
            + "\n"
            + "public Builder3 setId(Integer id) {\n"
            + "  return new Builder3(builder.setId(id));\n"
            + "}",
        output);
  }

  @Test
  void builderClassContent_when_generatorUsedWithOptionalField_then_correctOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator =
        SafeBuilderGens.builderClassContent();
    final SafeBuilderPojoField field =
        new SafeBuilderPojoField(PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private final Builder builder;\n"
            + "\n"
            + "private OptBuilder2(Builder builder) {\n"
            + "  this.builder = builder;\n"
            + "}\n"
            + "\n"
            + "public OptBuilder3 setId(Integer id) {\n"
            + "  return new OptBuilder3(builder.setId(id));\n"
            + "}\n"
            + "\n"
            + "public OptBuilder3 setId(Optional<Integer> id) {\n"
            + "  return new OptBuilder3(id.map(builder::setId).orElse(builder));\n"
            + "}",
        output);
  }

  @Test
  void constructor_when_generatorUsedWithRequiredField_then_correctClassname() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator = SafeBuilderGens.constructor();
    final SafeBuilderPojoField field = new SafeBuilderPojoField(PojoFields.requiredId(), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private Builder2(Builder builder) {\n" + "  this.builder = builder;\n" + "}", output);
  }

  @Test
  void constructor_when_generatorUsedWithOptionalField_then_correctClassname() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator = SafeBuilderGens.constructor();
    final SafeBuilderPojoField field =
        new SafeBuilderPojoField(PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private OptBuilder2(Builder builder) {\n" + "  this.builder = builder;\n" + "}", output);
  }

  @Test
  void finalRequiredBuilder_when_generatorUsedWithSamplePojo_then_correctClassOutput() {
    final Generator<Pojo, PojoSettings> generator = SafeBuilderGens.finalRequiredBuilder();
    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static final class Builder2 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private Builder2(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder0 andAllOptionals() {\n"
            + "    return new OptBuilder0(builder);\n"
            + "  }\n"
            + "\n"
            + "  public Builder andOptionals() {\n"
            + "    return builder;\n"
            + "  }\n"
            + "\n"
            + "  public Customer build() {\n"
            + "    return builder.build();\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void finalOptionalBuilder_when_generatorUsedWithSamplePojo_then_correctClassOutput() {
    final Generator<Pojo, PojoSettings> generator = SafeBuilderGens.finalOptionalBuilder();
    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static final class OptBuilder1 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private OptBuilder1(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Customer build() {\n"
            + "    return builder.build();\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_generatorUsedWithOptionalField_then_correctClassOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final SafeBuilderPojoField field =
        new SafeBuilderPojoField(PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists("java.lang.Integer"::equals));
    assertEquals(
        "public OptBuilder3 setId(Integer id) {\n"
            + "  return new OptBuilder3(builder.setId(id));\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_generatorUsedWithRequiredField_then_correctClassOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final SafeBuilderPojoField field = new SafeBuilderPojoField(PojoFields.requiredId(), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists("java.lang.Integer"::equals));
    assertEquals(
        "public Builder3 setId(Integer id) {\n"
            + "  return new Builder3(builder.setId(id));\n"
            + "}",
        output);
  }

  @Test
  void setMethodOptional_when_generatorUsedWithOptionalField_then_correctClassOutput() {
    final Generator<SafeBuilderPojoField, PojoSettings> generator =
        SafeBuilderGens.setMethodOptional();
    final SafeBuilderPojoField field =
        new SafeBuilderPojoField(PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists("java.lang.Integer"::equals));
    assertEquals(
        "public OptBuilder3 setId(Optional<Integer> id) {\n"
            + "  return new OptBuilder3(id.map(builder::setId).orElse(builder));\n"
            + "}",
        output);
  }
}
