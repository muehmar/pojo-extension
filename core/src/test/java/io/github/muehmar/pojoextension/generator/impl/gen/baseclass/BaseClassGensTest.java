package io.github.muehmar.pojoextension.generator.impl.gen.baseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class BaseClassGensTest {

  @Test
  void baseClass_when_samplePojo_then_correctBaseClass() {
    final Generator<Pojo, PojoSettings> gen = BaseClassGens.baseClass();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package io.github.muehmar;\n"
            + "\n"
            + "\n"
            + "abstract class CustomerBase implements CustomerExtension {\n"
            + "  @Override\n"
            + "  public boolean equals(Object o) {\n"
            + "    return genEquals(o);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public int hashCode() {\n"
            + "    return genHashCode();\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public String toString() {\n"
            + "    return genToString();\n"
            + "  }\n"
            + "}",
        writer.asString());
  }

  @Test
  void baseClass_when_genericSamplePojo_then_correctBaseClass() {
    final Generator<Pojo, PojoSettings> gen = BaseClassGens.baseClass();
    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package io.github.muehmar;\n"
            + "\n"
            + "import java.util.List;\n"
            + "\n"
            + "abstract class CustomerBase<T extends List<String>, S> implements CustomerExtension<T, S> {\n"
            + "  @Override\n"
            + "  public boolean equals(Object o) {\n"
            + "    return genEquals(o);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public int hashCode() {\n"
            + "    return genHashCode();\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public String toString() {\n"
            + "    return genToString();\n"
            + "  }\n"
            + "}",
        writer.asString());
  }
}
