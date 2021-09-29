package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class MethodGenTest {
  @Test
  void generate_when_minimalGeneratorCreated_then_outputCorrect() {
    final MethodGen<PList<String>, Void> generator =
        new MethodGen<>(
            (l, ignore) -> JavaModifiers.of(JavaModifier.PUBLIC, JavaModifier.FINAL),
            (l, ignore) -> l.apply(0),
            (l, ignore) -> l.apply(1),
            (l, ignore) -> l.drop(2),
            (l, ignore, w) -> w.println("System.out.println(\"Hello World\");"));

    final PList<String> data = PList.of("void", "getXY", "String a", "int b");

    final String output = generator.generate(data, null, WriterImpl.createDefault()).asString();
    assertEquals(
        "public final void getXY(String a, int b) {\n"
            + "  System.out.println(\"Hello World\");\n"
            + "}\n",
        output);
  }
}
