package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.WriterFactory;
import org.junit.jupiter.api.Test;

class ConstructorGenTest {
  @Test
  void generate_when_minimalGeneratorCreated_then_outputCorrect() {
    final ConstructorGen<PList<String>, Void> generator =
        new ConstructorGen<>(
            JavaModifiers.of(JavaModifier.PUBLIC),
            (l, ignore) -> l.apply(0),
            (l, ignore) -> l.drop(1),
            (l, ignore, w) -> w.println("System.out.println(\"Hello World\");"));

    final PList<String> data = PList.of("Customer", "String a", "int b");

    final String output = generator.generate(data, null, WriterFactory.createDefault()).asString();
    assertEquals(
        "public Customer(String a, int b) {\n" + "  System.out.println(\"Hello World\");\n" + "}\n",
        output);
  }
}
