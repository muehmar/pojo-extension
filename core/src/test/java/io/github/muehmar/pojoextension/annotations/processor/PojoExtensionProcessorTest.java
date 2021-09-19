package io.github.muehmar.pojoextension.annotations.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.data.Name;
import io.github.muehmar.pojoextension.data.PackageName;
import io.github.muehmar.pojoextension.data.Pojo;
import io.github.muehmar.pojoextension.data.PojoMember;
import io.github.muehmar.pojoextension.data.Type;
import io.github.muehmar.pojoextension.generator.PojoSettings;
import java.util.concurrent.atomic.AtomicReference;
import org.joor.CompileOptions;
import org.joor.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PojoExtensionProcessorTest {

  @Test
  void run_when_simplePojo_then_correctPojoCreated() {
    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            "io.github.muehmar.CustomerA",
            "package io.github.muehmar;\n"
                + "import io.github.muehmar.pojoextension.annotations.PojoExtension;"
                + "@PojoExtension\n"
                + "public class CustomerA {\n"
                + "  private final String id;\n"
                + "  public CustomerA(String id){\n"
                + "    this.id = id;\n"
                + "  }\n"
                + "}");

    final PojoMember m1 = new PojoMember(Type.string(), Name.fromString("id"), true);
    final Pojo expected =
        new Pojo(
            Name.fromString("CustomerAExtension"),
            Name.fromString("CustomerA"),
            PackageName.fromString("io.github.muehmar"),
            PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_oneOptionalMember_then_pojoMemberIsOptionalAndTypeParameterUsedAsType() {
    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            "io.github.muehmar.CustomerB",
            "package io.github.muehmar;\n"
                + "import io.github.muehmar.pojoextension.annotations.PojoExtension;"
                + "import java.util.Optional;"
                + "@PojoExtension\n"
                + "public class CustomerB {\n"
                + "  private final Optional<String> id;\n"
                + "  public CustomerB(String id){\n"
                + "    this.id = Optional.ofNullable(id);\n"
                + "  }\n"
                + "}");

    final PojoMember m1 = new PojoMember(Type.string(), Name.fromString("id"), false);
    final Pojo expected =
        new Pojo(
            Name.fromString("CustomerBExtension"),
            Name.fromString("CustomerB"),
            PackageName.fromString("io.github.muehmar"),
            PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_memberAnnotatedWithNullable_then_pojoMemberIsOptional() {
    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            "io.github.muehmar.CustomerC",
            "package io.github.muehmar;\n"
                + "import io.github.muehmar.pojoextension.annotations.PojoExtension;"
                + "import io.github.muehmar.pojoextension.annotations.Nullable;"
                + "import java.util.Optional;"
                + "@PojoExtension\n"
                + "public class CustomerC {\n"
                + "  @Nullable\n"
                + "  private final String id;\n"
                + "  public CustomerC(String id){\n"
                + "    this.id = id;\n"
                + "  }\n"
                + "}");

    final PojoMember m1 = new PojoMember(Type.string(), Name.fromString("id"), false);
    final Pojo expected =
        new Pojo(
            Name.fromString("CustomerCExtension"),
            Name.fromString("CustomerC"),
            PackageName.fromString("io.github.muehmar"),
            PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  private static PojoAndSettings runAnnotationProcessor(String name, String content) {
    final AtomicReference<PojoAndSettings> ref = new AtomicReference<>();
    final PojoExtensionProcessor pojoExtensionProcessor =
        new PojoExtensionProcessor(
            ((pojo, settings) -> ref.set(new PojoAndSettings(pojo, settings))));

    try {
      Reflect.compile(name, content, new CompileOptions().processors(pojoExtensionProcessor));
    } catch (Exception e) {
      Assertions.fail("Compilation failed: " + e.getMessage());
    }
    final PojoAndSettings pojoAndSettings = ref.get();
    assertNotNull(pojoAndSettings, "Output not redirected");

    return pojoAndSettings;
  }

  private static class PojoAndSettings {
    private final Pojo pojo;
    private final PojoSettings settings;

    public PojoAndSettings(Pojo pojo, PojoSettings settings) {
      this.pojo = pojo;
      this.settings = settings;
    }
  }
}
