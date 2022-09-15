package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.model.settings.PojoSettings.defaultSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.impl.gen.Refs;
import io.github.muehmar.pojoextension.generator.model.ClassAccessLevelModifier;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SafeBuilderClassGensTest {

  @Test
  void createMethod_when_calledWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = SafeBuilderClassGens.createMethod();

    final Writer writer = gen.generate(Pojos.sample(), defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static Builder0 create() {\n" + "  return new Builder0(new Builder());\n" + "}",
        writer.asString());
  }

  @ParameterizedTest
  @EnumSource(ClassAccessLevelModifier.class)
  void safeBuilderClass_when_calledWithSamplePojo_then_correctOutput(
      ClassAccessLevelModifier accessLevelModifier) {
    final Generator<Pojo, PojoSettings> gen = SafeBuilderClassGens.safeBuilderClass();

    final Writer writer =
        gen.generate(
            Pojos.sample(),
            defaultSettings().withBuilderAccessLevel(accessLevelModifier),
            Writer.createDefault());

    final String accessModifier =
        accessLevelModifier.equals(ClassAccessLevelModifier.PUBLIC) ? "public " : "";

    assertEquals(
        "package io.github.muehmar;\n"
            + "\n"
            + "import java.util.Optional;\n"
            + "\n"
            + accessModifier
            + "final class CustomerBuilder {\n"
            + "\n"
            + "  private CustomerBuilder() {\n"
            + "  }\n"
            + "\n"
            + "  public static Builder0 create() {\n"
            + "    return new Builder0(new Builder());\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder {\n"
            + "    private Builder() {\n"
            + "    }\n"
            + "\n"
            + "    private Integer id;\n"
            + "    private String username;\n"
            + "    private String nickname;\n"
            + "\n"
            + "    private Builder id(Integer id) {\n"
            + "      this.id = id;\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    private Builder username(String username) {\n"
            + "      this.username = username;\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    public Builder nickname(String nickname) {\n"
            + "      this.nickname = nickname;\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    public Builder nickname(Optional<String> nickname) {\n"
            + "      this.nickname = nickname.orElse(null);\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    public Customer build() {\n"
            + "      final Customer instance =\n"
            + "          new Customer(id, username, nickname);\n"
            + "      return instance;\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder0 {\n"
            + "    private final Builder builder;\n"
            + "\n"
            + "    private Builder0(Builder builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public Builder1 id(Integer id) {\n"
            + "      return new Builder1(builder.id(id));\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder1 {\n"
            + "    private final Builder builder;\n"
            + "\n"
            + "    private Builder1(Builder builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public Builder2 username(String username) {\n"
            + "      return new Builder2(builder.username(username));\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder2 {\n"
            + "    private final Builder builder;\n"
            + "\n"
            + "    private Builder2(Builder builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public OptBuilder0 andAllOptionals() {\n"
            + "      return new OptBuilder0(builder);\n"
            + "    }\n"
            + "\n"
            + "    public Builder andOptionals() {\n"
            + "      return builder;\n"
            + "    }\n"
            + "\n"
            + "    public Customer build() {\n"
            + "      return builder.build();\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class OptBuilder0 {\n"
            + "    private final Builder builder;\n"
            + "\n"
            + "    private OptBuilder0(Builder builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public OptBuilder1 nickname(String nickname) {\n"
            + "      return new OptBuilder1(builder.nickname(nickname));\n"
            + "    }\n"
            + "\n"
            + "    public OptBuilder1 nickname(Optional<String> nickname) {\n"
            + "      return new OptBuilder1(builder.nickname(nickname));\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class OptBuilder1 {\n"
            + "    private final Builder builder;\n"
            + "\n"
            + "    private OptBuilder1(Builder builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public Customer build() {\n"
            + "      return builder.build();\n"
            + "    }\n"
            + "  }\n"
            + "}",
        writer.asString());
  }

  @Test
  void safeBuilderClass_when_genericPojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = SafeBuilderClassGens.safeBuilderClass();

    final Writer writer =
        gen.generate(Pojos.genericSample(), defaultSettings(), Writer.createDefault());
    assertEquals(
        "package io.github.muehmar;\n"
            + "\n"
            + "import java.util.List;\n"
            + "import java.util.Optional;\n"
            + "\n"
            + "public final class CustomerBuilder {\n"
            + "\n"
            + "  private CustomerBuilder() {\n"
            + "  }\n"
            + "\n"
            + "  public static <T extends List<String>, S> Builder0<T, S> create() {\n"
            + "    return new Builder0<>(new Builder<T, S>());\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder<T extends List<String>, S> {\n"
            + "    private Builder() {\n"
            + "    }\n"
            + "\n"
            + "    private String id;\n"
            + "    private T data;\n"
            + "    private S additionalData;\n"
            + "\n"
            + "    private Builder<T, S> id(String id) {\n"
            + "      this.id = id;\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    private Builder<T, S> data(T data) {\n"
            + "      this.data = data;\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    public Builder<T, S> additionalData(S additionalData) {\n"
            + "      this.additionalData = additionalData;\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    public Builder<T, S> additionalData(Optional<S> additionalData) {\n"
            + "      this.additionalData = additionalData.orElse(null);\n"
            + "      return this;\n"
            + "    }\n"
            + "\n"
            + "    public Customer<T, S> build() {\n"
            + "      final Customer<T, S> instance =\n"
            + "          new Customer<>(id, data, additionalData);\n"
            + "      return instance;\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder0<T extends List<String>, S> {\n"
            + "    private final Builder<T, S> builder;\n"
            + "\n"
            + "    private Builder0(Builder<T, S> builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public Builder1<T, S> id(String id) {\n"
            + "      return new Builder1<>(builder.id(id));\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder1<T extends List<String>, S> {\n"
            + "    private final Builder<T, S> builder;\n"
            + "\n"
            + "    private Builder1(Builder<T, S> builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public Builder2<T, S> data(T data) {\n"
            + "      return new Builder2<>(builder.data(data));\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class Builder2<T extends List<String>, S> {\n"
            + "    private final Builder<T, S> builder;\n"
            + "\n"
            + "    private Builder2(Builder<T, S> builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public OptBuilder0<T, S> andAllOptionals() {\n"
            + "      return new OptBuilder0<>(builder);\n"
            + "    }\n"
            + "\n"
            + "    public Builder<T, S> andOptionals() {\n"
            + "      return builder;\n"
            + "    }\n"
            + "\n"
            + "    public Customer<T, S> build() {\n"
            + "      return builder.build();\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class OptBuilder0<T extends List<String>, S> {\n"
            + "    private final Builder<T, S> builder;\n"
            + "\n"
            + "    private OptBuilder0(Builder<T, S> builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public OptBuilder1<T, S> additionalData(S additionalData) {\n"
            + "      return new OptBuilder1<>(builder.additionalData(additionalData));\n"
            + "    }\n"
            + "\n"
            + "    public OptBuilder1<T, S> additionalData(Optional<S> additionalData) {\n"
            + "      return new OptBuilder1<>(builder.additionalData(additionalData));\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public static final class OptBuilder1<T extends List<String>, S> {\n"
            + "    private final Builder<T, S> builder;\n"
            + "\n"
            + "    private OptBuilder1(Builder<T, S> builder) {\n"
            + "      this.builder = builder;\n"
            + "    }\n"
            + "\n"
            + "    public Customer<T, S> build() {\n"
            + "      return builder.build();\n"
            + "    }\n"
            + "  }\n"
            + "}",
        writer.asString());
  }

  @Test
  void createMethod_when_calledWithGenericSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = SafeBuilderClassGens.createMethod();

    final Writer writer =
        gen.generate(Pojos.genericSample(), defaultSettings(), Writer.createDefault());

    assertEquals(
        "public static <T extends List<String>, S> Builder0<T, S> create() {\n"
            + "  return new Builder0<>(new Builder<T, S>());\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().exists(Refs.JAVA_UTIL_LIST::equals));
  }
}
