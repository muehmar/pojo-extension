package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class ClassGeneratorTest {

  @Test
  void generate_when_simplePojoAndSingleContent_then_correctGeneratedString() {
    final ClassGenerator generator =
        ClassGenerator.ofContent(Generator.ofWriterFunction(w -> w.println("Content")));

    final Writer writer =
        generator.generate(
            Pojos.sample(), PojoSettings.defaultSettings(), WriterImpl.createDefault());
    assertEquals(
        "package io.github.muehmar;\n"
            + "\n"
            + "\n"
            + "public class CustomerExtension {\n"
            + "  Content\n"
            + "}\n",
        writer.asString());
  }

  @Test
  void generate_when_refAddedInContent_then_refPrinted() {
    final ClassGenerator generator =
        ClassGenerator.ofContent(
            Generator.ofWriterFunction(w -> w.ref("import java.util.Optional;")));

    final Writer writer =
        generator.generate(
            Pojos.sample(), PojoSettings.defaultSettings(), WriterImpl.createDefault());
    assertEquals(
        "package io.github.muehmar;\n"
            + "\n"
            + "import java.util.Optional;\n"
            + "\n"
            + "public class CustomerExtension {\n"
            + "}\n",
        writer.asString());
  }
}
