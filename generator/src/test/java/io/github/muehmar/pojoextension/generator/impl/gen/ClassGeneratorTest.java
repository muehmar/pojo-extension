package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class ClassGeneratorTest {

  @Test
  void generate_when_simplePojoAndSingleContent_then_correctGeneratedString() {
    final ClassGenerator generator =
        ClassGenerator.ofContent(Generator.ofWriterFunction(w -> w.println("Content")));
    final Pojo pojo =
        new Pojo(
            Name.fromString("CustomerExtension"),
            Name.fromString("Customer"),
            PackageName.fromString("io.github.muehmar"),
            PList.empty());

    final PojoSettings pojoSettings = new PojoSettings(false);

    final Writer writer = generator.generate(pojo, pojoSettings, WriterImpl.createDefault());
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
    final Pojo pojo =
        new Pojo(
            Name.fromString("CustomerExtension"),
            Name.fromString("Customer"),
            PackageName.fromString("io.github.muehmar"),
            PList.empty());

    final PojoSettings pojoSettings = new PojoSettings(false);

    final Writer writer = generator.generate(pojo, pojoSettings, WriterImpl.createDefault());
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
