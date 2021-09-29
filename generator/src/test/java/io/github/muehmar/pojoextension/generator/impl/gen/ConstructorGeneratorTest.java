package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class ConstructorGeneratorTest {
  @Test
  void generate_when_minimalGeneratorCreated_then_outputCorrect() {
    final ConstructorGenerator<PList<String>, Void> generator =
        new ConstructorGenerator<>(
            JavaModifiers.of(JavaModifier.PUBLIC),
            (l, ignore) -> l.apply(0),
            (l, ignore) -> l.drop(1),
            (l, ignore, w) -> w.println("System.out.println(\"Hello World\");"));

    final PList<String> data = PList.of("Customer", "String a", "int b");

    final String output = generator.generate(data, null, WriterImpl.createDefault()).asString();
    assertEquals(
        "public Customer(String a, int b) {\n" + "  System.out.println(\"Hello World\");\n" + "}\n",
        output);
  }
}
