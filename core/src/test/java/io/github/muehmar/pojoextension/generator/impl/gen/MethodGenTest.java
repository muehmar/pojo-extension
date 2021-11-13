package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.Settings.noSettings;
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
            .noGenericTypes()
            .returnType(l -> l.apply(0))
            .methodName(l -> l.apply(1))
            .arguments(l -> l.drop(2))
            .contentWriter(w -> w.println("System.out.println(\"Hello World\");"));

    final PList<String> data = PList.of("void", "getXY", "String a", "int b");

    final String output = generator.generate(data, noSettings(), Writer.createDefault()).asString();
    assertEquals(
        "public final void getXY(String a, int b) {\n"
            + "  System.out.println(\"Hello World\");\n"
            + "}",
        output);
  }

  @Test
  void generate_when_methodWithGenerics_then_outputCorrect() {
    final MethodGen<String, Void> generator =
        MethodGen.<String, Void>modifiers(PUBLIC, FINAL)
            .genericTypes("T, S")
            .returnType("T")
            .methodName("doSomething")
            .singleArgument(ignore -> "S s")
            .contentWriter(w -> w.println("return s.getT();"));

    final String output =
        generator.generate("data", noSettings(), Writer.createDefault()).asString();
    assertEquals(
        "public final <T, S> T doSomething(S s) {\n" + "  return s.getT();\n" + "}", output);
  }
}
