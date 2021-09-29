package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class SetMethodGeneratorTest {
  @Test
  void generate_when_requiredField_then_correctPrivateMethodGenerated() {
    final SetMethodGenerator generator =
        new SetMethodGenerator(
            Generator.ofWriterFunction(w -> w.println("System.out.println(\"Hello World!\");")));

    final String output =
        generator
            .generate(
                PojoFields.requiredId(), PojoSettings.defaultSettings(), WriterImpl.createDefault())
            .asString();

    assertEquals(
        "private Builder setId(Integer id) {\n"
            + "  System.out.println(\"Hello World!\");\n"
            + "}\n",
        output);
  }

  @Test
  void generate_when_optionalField_then_correctPublicMethodGenerated() {
    final SetMethodGenerator generator =
        new SetMethodGenerator(
            Generator.ofWriterFunction(w -> w.println("System.out.println(\"Hello World!\");")));

    final String output =
        generator
            .generate(
                PojoFields.requiredId().withRequired(false),
                PojoSettings.defaultSettings(),
                WriterImpl.createDefault())
            .asString();

    assertEquals(
        "public Builder setId(Integer id) {\n"
            + "  System.out.println(\"Hello World!\");\n"
            + "}\n",
        output);
  }
}
