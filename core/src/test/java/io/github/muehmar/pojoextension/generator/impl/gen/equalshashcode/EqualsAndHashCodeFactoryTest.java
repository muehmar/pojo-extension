package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class EqualsAndHashCodeFactoryTest {
  @Test
  void equalsMethod_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsAndHashCodeFactory.equalsMethod();

    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();
    assertEquals(
        "public static boolean equals(Customer o1, Object obj) {\n"
            + "  if (o1 == obj) return true;\n"
            + "  if (obj == null) || o1.getClass() != obj.getClass()) return false;\n"
            + "  final Customer o2 = (Customer) obj;\n"
            + "  return Object.equals(o1.getId(), o2.getId())\n"
            + "      && Object.equals(o1.getUsername(), o2.getUsername())\n"
            + "      && Object.equals(o1.getNickname(), o2.getNickname());\n"
            + "}\n",
        output);
  }

  @Test
  void hashCodeMethod_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsAndHashCodeFactory.hashCodeMethod();

    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();
    assertEquals(
        "public static int hashCode(Customer o) {\n"
            + "  return Objects.hashCode(o.getId(), o.getUsername(), o.getNickname());\n"
            + "}\n",
        output);
  }
}
