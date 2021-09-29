package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class SetMethodContentGenTest {
  @Test
  void generate_when_fieldAsInput_then_correctOutput() {
    final SetMethodContentGen generator = new SetMethodContentGen();
    final PojoField field = Pojos.sample().getFields().apply(0);
    final String content =
        generator
            .generate(field, PojoSettings.defaultSettings(), WriterImpl.createDefault())
            .asString();

    assertEquals("this.id = id;\n" + "return this;\n", content);
  }
}
