package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.WriterImpl;
import org.junit.jupiter.api.Test;

class PackageGeneratorTest {

  @Test
  void generate_when_called_then_correctPackageStatementCreated() {
    final Pojo pojo =
        new Pojo(
            Name.fromString("DoesNotMatter"),
            Name.fromString("DoesNotMatter"),
            PackageName.fromString("io.github.muehmar"),
            PList.empty());
    final PackageGenerator generator = new PackageGenerator();
    final Writer writer =
        generator.generate(pojo, new PojoSettings(false), WriterImpl.createDefault());

    assertEquals("package io.github.muehmar;\n", writer.asString());
  }
}
