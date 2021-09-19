package io.github.muehmar.pojoextension.annotations.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.data.Name;
import io.github.muehmar.pojoextension.data.PackageName;
import io.github.muehmar.pojoextension.data.Pojo;
import io.github.muehmar.pojoextension.data.PojoMember;
import io.github.muehmar.pojoextension.data.Type;
import io.github.muehmar.pojoextension.generator.PojoSettings;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.joor.CompileOptions;
import org.joor.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PojoExtensionProcessorTest {

  private static final PackageName PACKAGE = PackageName.fromString("io.github.muehmar");

  @Test
  void run_when_simplePojo_then_correctPojoCreated() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("String", "id")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            className.prefix(".").prefix(PACKAGE.asString()).asString(), classString);

    final PojoMember m1 = new PojoMember(Type.string(), Name.fromString("id"), true);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_oneOptionalMember_then_pojoMemberIsOptionalAndTypeParameterUsedAsType() {
    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            "io.github.muehmar.CustomerB",
            "package io.github.muehmar;\n"
                + "import io.github.muehmar.pojoextension.annotations.PojoExtension;\n"
                + "import java.util.Optional;\n"
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
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(Nullable.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("String", "id", Nullable.class)
            .constructor()
            .create();
    System.out.println(classString);

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            className.prefix(".").prefix(PACKAGE.asString()).asString(), classString);

    final PojoMember m1 = new PojoMember(Type.string(), Name.fromString("id"), false);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @ParameterizedTest
  @EnumSource(OptionalDetection.class)
  void
      run_when_memberAnnotatedWithNullableAndDifferentDetection_then_pojoMemberIsOptionalOnlyIfNullableAnnotationDetection(
          OptionalDetection optionalDetection) {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(Nullable.class)
            .annotation(
                PojoExtension.class,
                "optionalDetection",
                OptionalDetection.class,
                optionalDetection)
            .className(className)
            .withField("String", "id", Nullable.class)
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            className.prefix(".").prefix(PACKAGE.asString()).asString(), classString);

    final boolean required = !optionalDetection.equals(OptionalDetection.NULLABLE_ANNOTATION);
    final PojoMember m1 = new PojoMember(Type.string(), Name.fromString("id"), required);
    final Pojo expected =
        new Pojo(
            className.append("Extension"),
            className,
            PackageName.fromString("io.github.muehmar"),
            PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @ParameterizedTest
  @EnumSource(OptionalDetection.class)
  void
      run_when_optionalFieldAndDifferentDetection_then_pojoMemberIsOptionalOnlyIfOptionalClassDetection(
          OptionalDetection optionalDetection) {
    final Name className =
        Name.fromString("Customer").append(UUID.randomUUID().toString().replaceAll("-", ""));
    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(
            "io.github.muehmar." + className.asString(),
            "package io.github.muehmar;\n"
                + "import io.github.muehmar.pojoextension.annotations.PojoExtension;\n"
                + "import io.github.muehmar.pojoextension.annotations.Nullable;\n"
                + "import io.github.muehmar.pojoextension.annotations.OptionalDetection;\n"
                + "import java.util.Optional;\n"
                + "@PojoExtension(optionalDetection = OptionalDetection."
                + optionalDetection.name()
                + ")\n"
                + "public class "
                + className.asString()
                + " {\n"
                + "  private final Optional<String> id;\n"
                + "  public "
                + className.asString()
                + "(String id){\n"
                + "    this.id = Optional.ofNullable(id);\n"
                + "  }\n"
                + "}");

    final boolean required = !optionalDetection.equals(OptionalDetection.OPTIONAL_CLASS);
    final Type type = required ? Type.optional(Type.string()) : Type.string();

    final PojoMember m1 = new PojoMember(type, Name.fromString("id"), required);
    final Pojo expected =
        new Pojo(
            className.append("Extension"),
            className,
            PackageName.fromString("io.github.muehmar"),
            PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  private static Name randomClassName() {
    return Name.fromString("Customer")
        .append(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
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
