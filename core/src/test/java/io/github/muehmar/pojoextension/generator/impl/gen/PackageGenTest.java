package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import org.junit.jupiter.api.Test;

class PackageGenTest {

  @Test
  void generate_when_called_then_correctPackageStatementCreated() {
    final PackageGen generator = new PackageGen();
    final Writer writer =
        generator.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("package io.github.muehmar;", writer.asString());
  }
}
