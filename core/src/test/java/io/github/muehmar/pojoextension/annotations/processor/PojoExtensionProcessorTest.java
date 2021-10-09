package io.github.muehmar.pojoextension.annotations.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import java.util.Optional;
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
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField m1 = new PojoField(Type.string(), Name.fromString("id"), true);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_oneOptionalField_then_pojoFieldIsOptionalAndTypeParameterUsedAsType() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(Optional.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("Optional<String>", "id")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField m1 = new PojoField(Type.string(), Name.fromString("id"), false);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_fieldAnnotatedWithNullable_then_pojoFieldIsOptional() {
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

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField m1 = new PojoField(Type.string(), Name.fromString("id"), false);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @ParameterizedTest
  @EnumSource(OptionalDetection.class)
  void
      run_when_fieldAnnotatedWithNullableAndDifferentDetection_then_pojoFieldIsOptionalOnlyIfNullableAnnotationDetection(
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
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final boolean required = !optionalDetection.equals(OptionalDetection.NULLABLE_ANNOTATION);
    final PojoField m1 = new PojoField(Type.string(), Name.fromString("id"), required);
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
      run_when_optionalFieldAndDifferentDetection_then_pojoFieldIsOptionalOnlyIfOptionalClassDetection(
          OptionalDetection optionalDetection) {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(Optional.class)
            .annotation(
                PojoExtension.class,
                "optionalDetection",
                OptionalDetection.class,
                optionalDetection)
            .className(className)
            .withField("Optional<String>", "id")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final boolean required = !optionalDetection.equals(OptionalDetection.OPTIONAL_CLASS);
    final Type type = required ? Type.optional(Type.string()) : Type.string();

    final PojoField m1 = new PojoField(type, Name.fromString("id"), required);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.single(m1));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_fieldWithPrimitiveType_then_() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("boolean", "flag")
            .withField("int", "count")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField f1 = new PojoField(Type.primitive("boolean"), Name.fromString("flag"), true);
    final PojoField f2 = new PojoField(Type.primitive("int"), Name.fromString("count"), true);
    final Pojo expected =
        new Pojo(className.append("Extension"), className, PACKAGE, PList.of(f1, f2));

    assertEquals(expected, pojoAndSettings.pojo);
  }

  private static Name randomClassName() {
    return Name.fromString("Customer")
        .append(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
  }

  private static Name qualifiedClassName(Name className) {
    return className.prefix(".").prefix(PACKAGE.asString());
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
