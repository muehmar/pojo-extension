package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.Resources;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.WriterFactory;
import org.junit.jupiter.api.Test;

class ExtensionFactoryTest {
  @Test
  void extensionClass_when_generatorUsedWithSamplePojo_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionFactory.extensionClass();

    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), WriterFactory.createDefault())
            .asString();

    assertEquals(readResourcePojoTemplate("SamplePojo"), output);
  }

  private static String readResourcePojoTemplate(String template) {
    return Resources.readString("/java/pojos/" + template + ".java.template");
  }
}
