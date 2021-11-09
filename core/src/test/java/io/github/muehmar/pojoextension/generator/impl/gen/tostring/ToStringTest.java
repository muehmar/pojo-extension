package io.github.muehmar.pojoextension.generator.impl.gen.tostring;

import static io.github.muehmar.pojoextension.generator.Settings.noSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ToStringTest {
  @Test
  void toStringMethod_when_called_then_correctDelegatedCall() {
    final Generator<Pojo, Void> gen = ToString.toStringMethod();

    final Writer writer = gen.generate(Pojos.sample(), noSettings(), Writer.createDefault());

    assertEquals(
        "@Override\n" + "public String toString() {\n" + "  return toString(self());\n" + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, Void> gen = ToString.staticToStringMethod();

    final Writer writer = gen.generate(Pojos.sample(), noSettings(), Writer.createDefault());

    assertEquals("public static String toString(Customer o) {\n" + "}", writer.asString());
  }
}
