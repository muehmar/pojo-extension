package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.Refs;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class CompleteSafeBuilderGensTest {
  @Test
  void completeSafeBuilder_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = CompleteSafeBuilderGens.completeSafeBuilder();
    final Writer writer =
        generator.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

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
            + "    return new Customer(id, username, nickname);\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder0 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private Builder0(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder1 id(Integer id) {\n"
            + "    return new Builder1(builder.id(id));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder1 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private Builder1(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder2 username(String username) {\n"
            + "    return new Builder2(builder.username(username));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder2 {\n"
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
            + "}\n"
            + "\n"
            + "public static final class OptBuilder0 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private OptBuilder0(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder1 nickname(String nickname) {\n"
            + "    return new OptBuilder1(builder.nickname(nickname));\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder1 nickname(Optional<String> nickname) {\n"
            + "    return new OptBuilder1(nickname.map(builder::nickname).orElse(builder));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class OptBuilder1 {\n"
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
        writer.asString());

    assertTrue(writer.getRefs().exists(Refs.JAVA_LANG_INTEGER::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void completeSafeBuilder_when_samplePojoAndBuilderSetMethodPrefix_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = CompleteSafeBuilderGens.completeSafeBuilder();
    final Writer writer =
        generator.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set")),
            Writer.createDefault());

    assertEquals(
        "public static final class Builder {\n"
            + "  private Builder() {\n"
            + "  }\n"
            + "\n"
            + "  private Integer id;\n"
            + "  private String username;\n"
            + "  private String nickname;\n"
            + "\n"
            + "  private Builder setId(Integer id) {\n"
            + "    this.id = id;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  private Builder setUsername(String username) {\n"
            + "    this.username = username;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Builder setNickname(String nickname) {\n"
            + "    this.nickname = nickname;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Builder setNickname(Optional<String> nickname) {\n"
            + "    this.nickname = nickname.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public Customer build() {\n"
            + "    return new Customer(id, username, nickname);\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder0 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private Builder0(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder1 setId(Integer id) {\n"
            + "    return new Builder1(builder.setId(id));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder1 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private Builder1(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder2 setUsername(String username) {\n"
            + "    return new Builder2(builder.setUsername(username));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder2 {\n"
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
            + "}\n"
            + "\n"
            + "public static final class OptBuilder0 {\n"
            + "  private final Builder builder;\n"
            + "\n"
            + "  private OptBuilder0(Builder builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder1 setNickname(String nickname) {\n"
            + "    return new OptBuilder1(builder.setNickname(nickname));\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder1 setNickname(Optional<String> nickname) {\n"
            + "    return new OptBuilder1(nickname.map(builder::setNickname).orElse(builder));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class OptBuilder1 {\n"
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
        writer.asString());

    assertTrue(writer.getRefs().exists(Refs.JAVA_LANG_INTEGER::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void completeSafeBuilder_when_genericPojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = CompleteSafeBuilderGens.completeSafeBuilder();
    final Writer writer =
        generator.generate(
            Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

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
            + "    return new Customer<>(id, data, additionalData);\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder0<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private Builder0(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder1<T, S> id(String id) {\n"
            + "    return new Builder1<>(builder.id(id));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder1<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private Builder1(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public Builder2<T, S> data(T data) {\n"
            + "    return new Builder2<>(builder.data(data));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class Builder2<T extends List<String>, S> {\n"
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
            + "}\n"
            + "\n"
            + "public static final class OptBuilder0<T extends List<String>, S> {\n"
            + "  private final Builder<T, S> builder;\n"
            + "\n"
            + "  private OptBuilder0(Builder<T, S> builder) {\n"
            + "    this.builder = builder;\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder1<T, S> additionalData(S additionalData) {\n"
            + "    return new OptBuilder1<>(builder.additionalData(additionalData));\n"
            + "  }\n"
            + "\n"
            + "  public OptBuilder1<T, S> additionalData(Optional<S> additionalData) {\n"
            + "    return new OptBuilder1<>(additionalData.map(builder::additionalData).orElse(builder));\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "public static final class OptBuilder1<T extends List<String>, S> {\n"
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

    assertTrue(writer.getRefs().exists(Refs.JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void completeSafeBuilder_when_safeBuilderDisabled_then_emptyOutput() {
    final Generator<Pojo, PojoSettings> generator = CompleteSafeBuilderGens.completeSafeBuilder();
    final String output =
        generator
            .generate(
                Pojos.sample(),
                PojoSettings.defaultSettings().withSafeBuilderAbility(DISABLED),
                Writer.createDefault())
            .asString();

    assertEquals("", output);
  }

  @Test
  void newBuilderMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = CompleteSafeBuilderGens.newBuilderMethod();
    final String output =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(
        "public static Builder0 newBuilder() {\n" + "  return new Builder0(new Builder());\n" + "}",
        output);
  }

  @Test
  void newBuilderMethod_when_safeBuilderDisabled_then_emptyOutput() {
    final Generator<Pojo, PojoSettings> gen = CompleteSafeBuilderGens.newBuilderMethod();
    final String output =
        gen.generate(
                Pojos.sample(),
                PojoSettings.defaultSettings().withSafeBuilderAbility(DISABLED),
                Writer.createDefault())
            .asString();

    assertEquals("", output);
  }

  @Test
  void newBuilderMethod_when_calledWithGenericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = CompleteSafeBuilderGens.newBuilderMethod();
    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static <T extends List<String>, S> Builder0<T, S> newBuilder() {\n"
            + "  return new Builder0<>(new Builder<T, S>());\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().exists(Refs.JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(Refs.JAVA_LANG_STRING::equals));
  }
}
