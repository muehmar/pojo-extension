package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterFactory;
import org.junit.jupiter.api.Test;

class SetMethodOptionalGenTest {

  @Test
  void generate_when_optionalField_then_correctPublicMethodGenerated() {
    final SetMethodOptionalGen generator = new SetMethodOptionalGen();

    final String output =
        generator
            .generate(
                PojoFields.requiredId().withRequired(false),
                PojoSettings.defaultSettings(),
                WriterFactory.createDefault())
            .asString();

    assertEquals(
        "public Builder setId(Optional<Integer> id) {\n"
            + "  this.id = id.orElse(null);\n"
            + "  return this;\n"
            + "}\n",
        output);
  }
}
