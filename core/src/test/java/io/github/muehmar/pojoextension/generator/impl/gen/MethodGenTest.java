package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class MethodGenTest {
  @Test
  void generate_when_minimalGeneratorCreated_then_outputCorrect() {
    final MethodGen<PList<String>, Void> generator =
        MethodGen.<PList<String>, Void>modifiers(PUBLIC, FINAL)
            .returnType(l -> l.apply(0))
            .methodName(l -> l.apply(1))
            .arguments(l -> l.drop(2))
            .content(w -> w.println("System.out.println(\"Hello World\");"));

    final PList<String> data = PList.of("void", "getXY", "String a", "int b");

    final String output = generator.generate(data, null, Writer.createDefault()).asString();
    assertEquals(
        "public final void getXY(String a, int b) {\n"
            + "  System.out.println(\"Hello World\");\n"
            + "}",
        output);
  }
}
