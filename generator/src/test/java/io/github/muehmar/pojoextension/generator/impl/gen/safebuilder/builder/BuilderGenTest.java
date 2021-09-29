package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterFactory;
import org.junit.jupiter.api.Test;

class BuilderGenTest {
  @Test
  void generate_when_samplePojoAsInput_then_correctOutput() {
    final BuilderGen generator = new BuilderGen();
    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), WriterFactory.createDefault())
            .asString();

    assertEquals(
        "public static final class Builder {\n"
            + "  private Builder() {\n"
            + "  }\n"
            + "  \n"
            + "  private Integer id;\n"
            + "  private String username;\n"
            + "  private String nickname;\n"
            + "  \n"
            + "  private Builder setId(Integer id) {\n"
            + "    this.id = id;\n"
            + "    return this;\n"
            + "  }\n"
            + "  \n"
            + "  private Builder setUsername(String username) {\n"
            + "    this.username = username;\n"
            + "    return this;\n"
            + "  }\n"
            + "  \n"
            + "  public Builder setNickname(String nickname) {\n"
            + "    this.nickname = nickname;\n"
            + "    return this;\n"
            + "  }\n"
            + "  \n"
            + "  public Builder setNickname(Optional<String> nickname) {\n"
            + "    this.nickname = nickname.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "  \n"
            + "  public Customer build() {\n"
            + "    return new Customer(id, username, nickname);\n"
            + "  }\n"
            + "}\n",
        output);
  }
}
