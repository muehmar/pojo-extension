package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.Resources;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ExtensionGensTest {
  @Test
  void extensionClass_when_generatorUsedWithSamplePojo_then_correctClassGenerated() {
    final Generator<Pojo, PojoSettings> generator = ExtensionGens.extensionClass();

    final String output =
        generator
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault())
            .asString();

    assertEquals(readResourcePojoTemplate("SamplePojo"), output);
  }

  private static String readResourcePojoTemplate(String template) {
    return Resources.readString("/java/pojos/" + template + ".java.template");
  }
}
