package io.github.muehmar.pojoextension.annotations.processor;

import static io.github.muehmar.pojoextension.generator.data.Type.integer;
import static io.github.muehmar.pojoextension.generator.data.Type.primitiveBoolean;
import static io.github.muehmar.pojoextension.generator.data.Type.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Constructor;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import java.util.Map;
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

    final PojoField m1 = new PojoField(string(), Name.fromString("id"), true);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

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

    final PojoField m1 = new PojoField(string(), Name.fromString("id"), false);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

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

    final PojoField m1 = new PojoField(string(), Name.fromString("id"), false);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

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
    final PojoField m1 = new PojoField(string(), Name.fromString("id"), required);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        new Pojo(
            className,
            PackageName.fromString("io.github.muehmar"),
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

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
    final Type type = required ? Type.optional(string()) : string();

    final PojoField m1 = new PojoField(type, Name.fromString("id"), required);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(
                new Constructor(
                    className, PList.single(new Argument(Name.fromString("id"), Type.string())))),
            PList.empty());

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_primitiveFields_then_fieldsCorrectCreated() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("boolean", "b")
            .withField("int", "i")
            .withField("short", "s")
            .withField("long", "l")
            .withField("float", "f")
            .withField("double", "d")
            .withField("byte", "by")
            .withField("char", "c")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField f1 = new PojoField(Type.primitive("boolean"), Name.fromString("b"), true);
    final PojoField f2 = new PojoField(Type.primitive("int"), Name.fromString("i"), true);
    final PojoField f3 = new PojoField(Type.primitive("short"), Name.fromString("s"), true);
    final PojoField f4 = new PojoField(Type.primitive("long"), Name.fromString("l"), true);
    final PojoField f5 = new PojoField(Type.primitive("float"), Name.fromString("f"), true);
    final PojoField f6 = new PojoField(Type.primitive("double"), Name.fromString("d"), true);
    final PojoField f7 = new PojoField(Type.primitive("byte"), Name.fromString("by"), true);
    final PojoField f8 = new PojoField(Type.primitive("char"), Name.fromString("c"), true);
    final PList<PojoField> fields = PList.of(f1, f2, f3, f4, f5, f6, f7, f8);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_arrays_then_fieldsCorrectCreated() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(Map.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("Map<String, Integer>[]", "data")
            .withField("byte[]", "key")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField f1 =
        new PojoField(
            Type.map(string(), integer()).withIsArray(true), Name.fromString("data"), true);
    final PojoField f2 =
        new PojoField(Type.primitive("byte").withIsArray(true), Name.fromString("key"), true);
    final PList<PojoField> fields = PList.of(f1, f2);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

    assertEquals(expected, pojoAndSettings.pojo);
  }

  @Test
  void run_when_gettersPresent_then_extractOnlyGettersCorrectly() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(Optional.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("Optional<String>", "data")
            .withField("Integer", "key")
            .constructor()
            .getter("Optional<String>", "data")
            .getter("Integer", "key")
            .method("void", "doSomething", "")
            .method("boolean", "isFlag", "return true")
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PList<Getter> expected =
        PList.of(
            Getter.newBuilder()
                .setName(Name.fromString("getData"))
                .setReturnType(Type.optional(string()))
                .build(),
            Getter.newBuilder().setName(Name.fromString("getKey")).setReturnType(integer()).build(),
            Getter.newBuilder()
                .setName(Name.fromString("isFlag"))
                .setReturnType(primitiveBoolean())
                .build());

    assertEquals(expected, pojoAndSettings.pojo.getGetters());
  }

  @Test
  void run_when_constantDefined_then_constantIgnored() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("String", "id")
            .withConstant("String", "CONSTANT_VAL=\"123456\"")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className).asString(), classString);

    final PojoField m1 = new PojoField(string(), Name.fromString("id"), true);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        new Pojo(
            className,
            PACKAGE,
            fields,
            PList.single(new Constructor(className, fields.map(PojoFields::toArgument))),
            PList.empty());

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
