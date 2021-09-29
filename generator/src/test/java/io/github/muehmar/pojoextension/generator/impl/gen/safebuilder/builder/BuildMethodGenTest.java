package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterFactory;
import org.junit.jupiter.api.Test;

class BuildMethodGenTest {
  @Test
  void generate_when_pojoSampleAsInput_then_correctOutput() {
    final BuildMethodGen generator = new BuildMethodGen();
    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), WriterFactory.createDefault())
            .asString();

    assertEquals(
        "public Customer build() {\n" + "  return new Customer(id, username);\n" + "}\n", output);
  }
}
