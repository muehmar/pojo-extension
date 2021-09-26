package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class FieldDeclarationGeneratorTest {
  @Test
  void test() {
    final FieldDeclarationGenerator generator = new FieldDeclarationGenerator();
    final Writer writer =
        generator.generate(
            new PojoField(Type.string(), Name.fromString("id"), true),
            new PojoSettings(false),
            WriterImpl.createDefault());

    assertEquals("private final String id;\n", writer.asString());
  }
}
