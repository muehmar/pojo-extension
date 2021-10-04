package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class CompleteSafeBuilderFactoryTest {
  @Test
  void completeSafeBuilder_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator =
        CompleteSafeBuilderFactory.completeSafeBuilder();
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
            + "public static Builder0 newBuilder() {\n"
            + "  return new Builder0(new Builder());\n"
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
            + "}\n",
        output);
  }
}
