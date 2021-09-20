package io.github.muehmar.pojoextension.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Resources;
import io.github.muehmar.pojoextension.data.Name;
import io.github.muehmar.pojoextension.data.PackageName;
import io.github.muehmar.pojoextension.data.Pojo;
import io.github.muehmar.pojoextension.data.PojoField;
import io.github.muehmar.pojoextension.data.Type;
import org.junit.jupiter.api.Test;

class JavaGeneratorTest {

  @Test
  void generate_when_simplePojo_then_outputMatchesTemplate() {
    final Generator generator = GeneratorFactory.create();
    final PojoField id = new PojoField(Type.string(), Name.fromString("id"), true);
    final PojoField name = new PojoField(Type.string(), Name.fromString("name"), false);
    final Pojo pojo =
        new Pojo(
            Name.fromString("CustomerExtension"),
            Name.fromString("Customer"),
            PackageName.fromString("io.github.muehmar"),
            PList.of(id, name));

    final String result = generator.generate(pojo, new PojoSettings(false));
    assertEquals(readResourcePojoTemplate("CustomerExtension"), result);
  }

  private static String readResourcePojoTemplate(String template) {
    return Resources.readString("/java/pojos/" + template + ".java.template");
  }
}
