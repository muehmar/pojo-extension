package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterFactory;
import org.junit.jupiter.api.Test;

class SetMethodGenTest {
  @Test
  void generate_when_requiredField_then_correctPrivateMethodGenerated() {
    final SetMethodGen generator = new SetMethodGen();

    final String output =
        generator
            .generate(
                PojoFields.requiredId(),
                PojoSettings.defaultSettings(),
                WriterFactory.createDefault())
            .asString();

    assertEquals(
        "private Builder setId(Integer id) {\n" + "  this.id = id;\n" + "  return this;\n" + "}\n",
        output);
  }

  @Test
  void generate_when_optionalField_then_correctPublicMethodGenerated() {
    final SetMethodGen generator = new SetMethodGen();

    final String output =
        generator
            .generate(
                PojoFields.requiredId().withRequired(false),
                PojoSettings.defaultSettings(),
                WriterFactory.createDefault())
            .asString();

    assertEquals(
        "public Builder setId(Integer id) {\n" + "  this.id = id;\n" + "  return this;\n" + "}\n",
        output);
  }
}