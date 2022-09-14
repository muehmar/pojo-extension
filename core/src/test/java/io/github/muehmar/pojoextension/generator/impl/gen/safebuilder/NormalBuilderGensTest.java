package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_INTEGER;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.Necessity.OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.BuildMethod;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoAndField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import org.junit.jupiter.api.Test;

class NormalBuilderGensTest {
  @Test
  void buildMethod_when_calledWithSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = NormalBuilderGens.buildMethod();
    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public Customer build() {\n"
            + "  final Customer instance =\n"
            + "      new Customer(id, username, nickname);\n"
            + "  return instance;\n"
            + "}",
        output);
  }

  @Test
  void buildMethod_when_calledWithGenericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = NormalBuilderGens.buildMethod();
    final String output =
        generator
            .generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public Customer<T, S> build() {\n"
            + "  final Customer<T, S> instance =\n"
            + "      new Customer<>(id, data, additionalData);\n"
            + "  return instance;\n"
            + "}",
        output);
  }

  @Test
  void buildMethod_when_calledWithGenericSampleAndBuildMethod_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = NormalBuilderGens.buildMethod();
    final BuildMethod buildMethod =
        new BuildMethod(Name.fromString("customBuildMethod"), Types.string());
    final Pojo pojo = Pojos.genericSample().withBuildMethod(buildMethod);
    final String output =
        generator.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault()).asString();

    assertEquals(
        "public String build() {\n"
            + "  final Customer<T, S> instance =\n"
            + "      new Customer<>(id, data, additionalData);\n"
            + "  return Customer.customBuildMethod(instance);\n"
            + "}",
        output);
  }

  @Test
  void
      buildMethod_when_generatorUsedWithSamplePojoAndConstructorWithOptionalArgument_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = NormalBuilderGens.buildMethod();
    final Writer writer =
        generator.generate(
            Pojos.sampleWithConstructorWithOptionalArgument(),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertEquals(
        "public Customer build() {\n"
            + "  final Customer instance =\n"
            + "      new Customer(id, username, Optional.ofNullable(nickname));\n"
            + "  return instance;\n"
            + "}",
        output);
  }

  @Test
  void setMethod_when_generatorUsedWithRequiredField_then_correctPrivateMethodGenerated() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethod();

    final PojoAndField pojoAndField = new PojoAndField(Pojos.sample(), PojoFields.requiredId());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "private Builder id(Integer id) {\n" + "  this.id = id;\n" + "  return this;\n" + "}",
        output);
  }

  @Test
  void setMethod_when_builderSetMethodPrefix_then_correctSetMethod() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethod();

    final PojoAndField pojoAndField = new PojoAndField(Pojos.sample(), PojoFields.requiredId());
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set"));

    final Writer writer = generator.generate(pojoAndField, settings, Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "private Builder setId(Integer id) {\n" + "  this.id = id;\n" + "  return this;\n" + "}",
        writer.asString());
  }

  @Test
  void setMethod_when_generatorUsedWithGenericType_then_correctRefs() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethod();

    final PojoAndField pojoAndField = new PojoAndField(Pojos.sample(), PojoFields.requiredMap());

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
  }

  @Test
  void setMethod_when_generatorUsedWithOptionalField_then_correctPublicMethodGenerated() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethod();

    final PojoAndField pojoAndField =
        new PojoAndField(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder id(Integer id) {\n" + "  this.id = id;\n" + "  return this;\n" + "}",
        output);
  }

  @Test
  void setMethod_when_fieldOfGenericClass_then_correctParameterizableBuilder() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethod();

    final PojoAndField pojoAndField =
        new PojoAndField(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());
    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder<T, S> id(Integer id) {\n" + "  this.id = id;\n" + "  return this;\n" + "}",
        output);
  }

  @Test
  void setMethodOptional_when_optionalField_then_correctPublicMethodGenerated() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethodOptional();

    final PojoAndField pojoAndField =
        new PojoAndField(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());

    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder id(Optional<Integer> id) {\n"
            + "  this.id = id.orElse(null);\n"
            + "  return this;\n"
            + "}",
        output);
  }

  @Test
  void setMethodOptional_when_builderSetMethodePrefix_then_correctPublicMethodGenerated() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethodOptional();

    final PojoAndField pojoAndField =
        new PojoAndField(Pojos.sample(), PojoFields.requiredId().withNecessity(OPTIONAL));
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set"));

    final Writer writer = generator.generate(pojoAndField, settings, Writer.createDefault());

    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder setId(Optional<Integer> id) {\n"
            + "  this.id = id.orElse(null);\n"
            + "  return this;\n"
            + "}",
        output);
  }

  @Test
  void setMethodOptional_when_optionalFieldWithGenericType_then_correctRefs() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethodOptional();

    final PojoAndField pojoAndField =
        new PojoAndField(Pojos.sample(), PojoFields.requiredMap().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void setMethodOptional_when_genericClass_then_correctPublicMethodGenerated() {
    final Generator<PojoAndField, PojoSettings> generator = NormalBuilderGens.setMethodOptional();

    final PojoAndField pojoAndField =
        new PojoAndField(Pojos.genericSample(), PojoFields.requiredId().withNecessity(OPTIONAL));

    final Writer writer =
        generator.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());

    final String output = writer.asString();

    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertEquals(
        "public Builder<T, S> id(Optional<Integer> id) {\n"
            + "  this.id = id.orElse(null);\n"
            + "  return this;\n"
            + "}",
        output);
  }

  @Test
  void builderClass_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = NormalBuilderGens.builderClass();
    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static final class Builder {\n"
            + "  private Builder() {\n"
            + "  }\n"
            + "\n"
            + "  private Integer id;\n"
            + "  private String username;\n"
            + "  private String nickname;\n"
            + "\n"
            + "  private Builder id(Integer id) {\n"
            + "    this.id = id;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  private Builder username(String username) {\n"
            + "    this.username = username;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Builder nickname(String nickname) {\n"
            + "    this.nickname = nickname;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Builder nickname(Optional<String> nickname) {\n"
            + "    this.nickname = nickname.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Customer build() {\n"
            + "    final Customer instance =\n"
            + "        new Customer(id, username, nickname);\n"
            + "    return instance;\n"
            + "  }\n"
            + "}",
        output);
  }

  @Test
  void builderClass_when_generatorUsedWithGenericSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = NormalBuilderGens.builderClass();
    final String output =
        generator
            .generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static final class Builder<T extends List<String>, S> {\n"
            + "  private Builder() {\n"
            + "  }\n"
            + "\n"
            + "  private String id;\n"
            + "  private T data;\n"
            + "  private S additionalData;\n"
            + "\n"
            + "  private Builder<T, S> id(String id) {\n"
            + "    this.id = id;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  private Builder<T, S> data(T data) {\n"
            + "    this.data = data;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Builder<T, S> additionalData(S additionalData) {\n"
            + "    this.additionalData = additionalData;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Builder<T, S> additionalData(Optional<S> additionalData) {\n"
            + "    this.additionalData = additionalData.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Customer<T, S> build() {\n"
            + "    final Customer<T, S> instance =\n"
            + "        new Customer<>(id, data, additionalData);\n"
            + "    return instance;\n"
            + "  }\n"
            + "}",
        output);
  }
}
