package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_INTEGER;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.Necessity.OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.FieldBuilderMethods;
import io.github.muehmar.pojoextension.generator.BuilderFields;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.BuilderField;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.FieldBuilder;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SafeBuilderGensTest {

  @Test
  void fieldBuilderClass_when_generatorUsedWithRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderClass();
    final BuilderField field = BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2);
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
            + "  public Builder3 id(Integer id) {\n"
            + "    return new Builder3(builder.id(id));\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void fieldBuilderClass_when_generatorUsedWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderClass();
    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
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
            + "  public OptBuilder3 id(Integer id) {\n"
            + "    return new OptBuilder3(builder.id(id));\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder3 id(Optional<Integer> id) {\n"
            + "    return new OptBuilder3(id.map(builder::id).orElse(builder));\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void fieldBuilderClass_when_genericSampleWithRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderClass();
    final BuilderField field = BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId(), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static final class Builder2<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private Builder2(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder3<T, S> id(Integer id) {\n"
            + "    return new Builder3<>(builder.id(id));\n"
            + "  }\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void fieldBuilderClass_when_genericSampleWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderClass();
    final BuilderField field =
        BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static final class OptBuilder2<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private OptBuilder2(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder3<T, S> id(Integer id) {\n"
            + "    return new OptBuilder3<>(builder.id(id));\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder3<T, S> id(Optional<Integer> id) {\n"
            + "    return new OptBuilder3<>(id.map(builder::id).orElse(builder));\n"
            + "  }\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void builderClassContent_when_generatorUsedWithRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.builderClassContent();
    final BuilderField field = BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2);
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
            + "public Builder3 id(Integer id) {\n"
            + "  return new Builder3(builder.id(id));\n"
            + "}",
        output);
  }

  @Test
  void builderClassContent_when_genericSampleWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.builderClassContent();
    final BuilderField field =
        BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private final Builder<T, S> builder;\n"
            + "\n"
            + "private OptBuilder2(Builder<T, S> builder) {\n"
            + "  this.builder = builder;\n"
            + "}\n"
            + "\n"
            + "public OptBuilder3<T, S> id(Integer id) {\n"
            + "  return new OptBuilder3<>(builder.id(id));\n"
            + "}\n"
            + "\n"
            + "public OptBuilder3<T, S> id(Optional<Integer> id) {\n"
            + "  return new OptBuilder3<>(id.map(builder::id).orElse(builder));\n"
            + "}",
        output);
  }

  @Test
  void builderClassContent_when_genericSampleWithRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.builderClassContent();
    final BuilderField field = BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId(), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private final Builder<T, S> builder;\n"
            + "\n"
            + "private Builder2(Builder<T, S> builder) {\n"
            + "  this.builder = builder;\n"
            + "}\n"
            + "\n"
            + "public Builder3<T, S> id(Integer id) {\n"
            + "  return new Builder3<>(builder.id(id));\n"
            + "}",
        output);
  }

  @Test
  void builderClassContent_when_generatorUsedWithFieldBuilderMethods_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.builderClassContent();
    final FieldBuilderMethod fieldBuilderMethod1 =
        new FieldBuilderMethod(
            Name.fromString("id"),
            Optional.empty(),
            Name.fromString("customMethod1"),
            Types.integer(),
            PList.empty());
    final FieldBuilderMethod fieldBuilderMethod2 =
        new FieldBuilderMethod(
            Name.fromString("id"),
            Optional.empty(),
            Name.fromString("customMethod2"),
            Types.integer(),
            PList.of(new Argument(Name.fromString("val"), Types.integer())));
    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2)
            .withFieldBuilder(
                new FieldBuilder(false, NonEmptyList.of(fieldBuilderMethod1, fieldBuilderMethod2)));
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
            + "public Builder3 id(Integer id) {\n"
            + "  return new Builder3(builder.id(id));\n"
            + "}\n"
            + "\n"
            + "public Builder3 customMethod1() {\n"
            + "  return new Builder3(builder.id(Customer.customMethod1()));\n"
            + "}\n"
            + "\n"
            + "public Builder3 customMethod2(Integer val) {\n"
            + "  return new Builder3(builder.id(Customer.customMethod2(val)));\n"
            + "}",
        output);
  }

  @Test
  void constructor_when_generatorUsedWithRequiredField_then_correctClassname() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.constructor();
    final BuilderField field = BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private Builder2(Builder builder) {\n" + "  this.builder = builder;\n" + "}", output);
  }

  @Test
  void constructor_when_generatorUsedWithOptionalField_then_correctClassname() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.constructor();
    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private OptBuilder2(Builder builder) {\n" + "  this.builder = builder;\n" + "}", output);
  }

  @Test
  void constructor_when_genericSampleWithRequiredField_then_correctClassname() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.constructor();
    final BuilderField field = BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId(), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private Builder2(Builder<T, S> builder) {\n" + "  this.builder = builder;\n" + "}",
        output);
  }

  @Test
  void constructor_when_genericSampleWithOptionalField_then_correctClassname() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.constructor();
    final BuilderField field =
        BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final String output =
        generator
            .generate(field, PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "private OptBuilder2(Builder<T, S> builder) {\n" + "  this.builder = builder;\n" + "}",
        output);
  }

  @Test
  void finalRequiredBuilder_when_samplePojo_then_correctClassOutput() {
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
  void finalRequiredBuilder_when_genericPojo_then_correctClassOutput() {
    final Generator<Pojo, PojoSettings> generator = SafeBuilderGens.finalRequiredBuilder();
    final Writer writer =
        generator.generate(
            Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static final class Builder2<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private Builder2(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder0<T, S> andAllOptionals() {\n"
            + "    return new OptBuilder0<>(builder);\n"
            + "  }\n"
            + "\n"
            + "  public Builder<T, S> andOptionals() {\n"
            + "    return builder;\n"
            + "  }\n"
            + "\n"
            + "  public Customer<T, S> build() {\n"
            + "    return builder.build();\n"
            + "  }\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void finalOptionalBuilder_when_samplePojo_then_correctClassOutput() {
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
  void finalOptionalBuilder_when_genericPojo_then_correctClassOutput() {
    final Generator<Pojo, PojoSettings> generator = SafeBuilderGens.finalOptionalBuilder();
    final Writer writer =
        generator.generate(
            Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static final class OptBuilder1<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private OptBuilder1(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Customer<T, S> build() {\n"
            + "    return builder.build();\n"
            + "  }\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void setMethod_when_generatorUsedWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public OptBuilder3 id(Integer id) {\n"
            + "  return new OptBuilder3(builder.id(id));\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_generatorUsedWithRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final BuilderField field = BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder3 id(Integer id) {\n" + "  return new Builder3(builder.id(id));\n" + "}",
        output);
  }

  @Test
  void setMethod_when_genericSampleWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final BuilderField field =
        BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public OptBuilder3<T, S> id(Integer id) {\n"
            + "  return new OptBuilder3<>(builder.id(id));\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_genericSampleWithRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final BuilderField field = BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId(), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder3<T, S> id(Integer id) {\n"
            + "  return new Builder3<>(builder.id(id));\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_builderSetMethodPrefix_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final BuilderField field = BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId(), 2);
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set"));

    final Writer writer = generator.generate(field, settings, Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder3<T, S> setId(Integer id) {\n"
            + "  return new Builder3<>(builder.setId(id));\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_generatorUsedWithGenericType_then_correctRefs() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethod();
    final PojoField field = PojoFields.requiredMap();
    final BuilderField builderField = BuilderFields.of(Pojos.sample(), field, 2);
    final Writer writer =
        generator.generate(builderField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void setMethodOptional_when_generatorUsedWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethodOptional();
    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public OptBuilder3 id(Optional<Integer> id) {\n"
            + "  return new OptBuilder3(id.map(builder::id).orElse(builder));\n"
            + "}",
        writer.asString());
  }

  @Test
  void setMethodOptional_when_builderSetMethodPrefix_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethodOptional();
    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set"));

    final Writer writer = generator.generate(field, settings, Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public OptBuilder3 setId(Optional<Integer> id) {\n"
            + "  return new OptBuilder3(id.map(builder::setId).orElse(builder));\n"
            + "}",
        writer.asString());
  }

  @Test
  void setMethodOptional_when_genericSampleWithOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethodOptional();
    final BuilderField field =
        BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL), 2);
    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public OptBuilder3<T, S> id(Optional<Integer> id) {\n"
            + "  return new OptBuilder3<>(id.map(builder::id).orElse(builder));\n"
            + "}",
        output);
  }

  @Test
  void setMethodOptional_when_generatorUsedWithGenericType_then_correctRefs() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.setMethodOptional();
    final PojoField field = PojoFields.requiredMap();
    final BuilderField builderField = BuilderFields.of(Pojos.sample(), field, 2);
    final Writer writer =
        generator.generate(builderField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
  }

  @Test
  void fieldBuilderMethods_when_noMethods_then_noOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderMethods();
    final BuilderField field = BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2);

    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertEquals("", output);
  }

  @Test
  void fieldBuilderMethods_when_singleMethodForRequiredField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderMethods();

    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            PojoFields.requiredId(),
            Name.fromString("customRandomId"),
            new Argument(Name.fromString("value"), Types.integer()));

    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredId(), 2)
            .withFieldBuilder(new FieldBuilder(false, NonEmptyList.single(fieldBuilderMethod)));

    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder3 customRandomId(Integer value) {\n"
            + "  return new Builder3(builder.id(Customer.customRandomId(value)));\n"
            + "}",
        output);
  }

  @Test
  void fieldBuilderMethods_when_singleMethodForOptionalField_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderMethods();

    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            PojoFields.optionalName(),
            Name.fromString("customRandomName"),
            new Argument(Name.fromString("value"), Types.string()));

    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.optionalName(), 2)
            .withFieldBuilder(new FieldBuilder(false, NonEmptyList.single(fieldBuilderMethod)));

    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertEquals(
        "public OptBuilder3 customRandomName(String value) {\n"
            + "  return new OptBuilder3(builder.name(Customer.customRandomName(value)));\n"
            + "}",
        output);
  }

  @Test
  void fieldBuilderMethods_when_singleMethodForGenericPojo_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderMethods();

    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            PojoFields.requiredId(),
            Name.fromString("customRandomId"),
            new Argument(Name.fromString("value"), Types.integer()));

    final BuilderField field =
        BuilderFields.of(Pojos.genericSample(), PojoFields.requiredId(), 2)
            .withFieldBuilder(new FieldBuilder(false, NonEmptyList.single(fieldBuilderMethod)));

    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder3<T, S> customRandomId(Integer value) {\n"
            + "  return new Builder3<>(builder.id(Customer.customRandomId(value)));\n"
            + "}",
        output);
  }

  @Test
  void fieldBuilderMethods_when_singleMethodWithInnerClass_then_correctOutput() {
    final Generator<BuilderField, PojoSettings> generator = SafeBuilderGens.fieldBuilderMethods();

    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                PojoFields.optionalName(),
                Name.fromString("customRandomName"),
                new Argument(Name.fromString("value"), Types.string()))
            .withInnerClassName(Name.fromString("NameBuilder"));

    final BuilderField field =
        BuilderFields.of(Pojos.sample(), PojoFields.optionalName(), 2)
            .withFieldBuilder(new FieldBuilder(false, NonEmptyList.single(fieldBuilderMethod)));

    final Writer writer =
        generator.generate(field, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertEquals(
        "public OptBuilder3 customRandomName(String value) {\n"
            + "  return new OptBuilder3(builder.name(Customer.NameBuilder.customRandomName(value)));\n"
            + "}",
        output);
  }
}
