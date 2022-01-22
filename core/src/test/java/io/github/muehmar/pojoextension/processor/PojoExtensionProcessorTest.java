package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.Type.integer;
import static io.github.muehmar.pojoextension.generator.data.Type.primitiveBoolean;
import static io.github.muehmar.pojoextension.generator.data.Type.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.Names;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Constructor;
import io.github.muehmar.pojoextension.generator.data.Generic;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.GetterBuilder;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Necessity;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoBuilder;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PojoExtensionProcessorTest extends BaseExtensionProcessorTest {

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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField m1 = new PojoField(Names.id(), string(), REQUIRED);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField m1 = new PojoField(Names.id(), string(), OPTIONAL);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField m1 = new PojoField(Names.id(), string(), OPTIONAL);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
            .annotationEnumParam(
                PojoExtension.class,
                "optionalDetection",
                OptionalDetection.class,
                optionalDetection)
            .className(className)
            .withField("String", "id", Nullable.class)
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final Necessity necessity =
        optionalDetection.equals(OptionalDetection.NULLABLE_ANNOTATION) ? OPTIONAL : REQUIRED;
    final PojoField m1 = new PojoField(Names.id(), string(), necessity);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PackageName.fromString("io.github.muehmar"))
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
            .annotationEnumParam(
                PojoExtension.class,
                "optionalDetection",
                OptionalDetection.class,
                optionalDetection)
            .className(className)
            .withField("Optional<String>", "id")
            .constructor()
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final Necessity required =
        optionalDetection.equals(OptionalDetection.OPTIONAL_CLASS) ? OPTIONAL : REQUIRED;
    final Type type = required.isRequired() ? Type.optional(string()) : string();

    final PojoField m1 = new PojoField(Names.id(), type, required);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(
                    new Constructor(
                        className, PList.single(new Argument(Names.id(), Type.string())))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField f1 = new PojoField(Name.fromString("b"), Type.primitive("boolean"), REQUIRED);
    final PojoField f2 = new PojoField(Name.fromString("i"), Type.primitive("int"), REQUIRED);
    final PojoField f3 = new PojoField(Name.fromString("s"), Type.primitive("short"), REQUIRED);
    final PojoField f4 = new PojoField(Name.fromString("l"), Type.primitive("long"), REQUIRED);
    final PojoField f5 = new PojoField(Name.fromString("f"), Type.primitive("float"), REQUIRED);
    final PojoField f6 = new PojoField(Name.fromString("d"), Type.primitive("double"), REQUIRED);
    final PojoField f7 = new PojoField(Name.fromString("by"), Type.primitive("byte"), REQUIRED);
    final PojoField f8 = new PojoField(Name.fromString("c"), Type.primitive("char"), REQUIRED);
    final PList<PojoField> fields = PList.of(f1, f2, f3, f4, f5, f6, f7, f8);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField f1 =
        new PojoField(
            Name.fromString("data"), Type.map(string(), integer()).withIsArray(true), REQUIRED);
    final PojoField f2 =
        new PojoField(Name.fromString("key"), Type.primitive("byte").withIsArray(true), REQUIRED);
    final PList<PojoField> fields = PList.of(f1, f2);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PList<Getter> expected =
        PList.of(
            GetterBuilder.create()
                .name(Name.fromString("getData"))
                .returnType(Type.optional(string()))
                .build(),
            GetterBuilder.create().name(Name.fromString("getKey")).returnType(integer()).build(),
            GetterBuilder.create()
                .name(Name.fromString("isFlag"))
                .returnType(primitiveBoolean())
                .build());

    assertEquals(expected, pojoAndSettings.getPojo().getGetters());
  }

  @Test
  void run_when_annotatedGetterPresent_then_extractGetterWithFieldName() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(io.github.muehmar.pojoextension.annotations.Getter.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("Integer", "key")
            .constructor()
            .getterWithAnnotation("Integer", "key", "@Getter(\"key\")")
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PList<Getter> expected =
        PList.of(
            GetterBuilder.create()
                .name(Name.fromString("getKey"))
                .returnType(Type.integer())
                .andOptionals()
                .fieldName(Name.fromString("key"))
                .build());

    assertEquals(expected, pojoAndSettings.getPojo().getGetters());
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
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField m1 = new PojoField(Names.id(), string(), REQUIRED);
    final PList<PojoField> fields = PList.single(m1);
    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.empty())
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
  }

  @Test
  void run_when_genericClass_then_correctGenerics() {
    final Name className = randomClassName();

    final String classString =
        "package "
            + PACKAGE
            + ";\n"
            + "import java.util.List;\n"
            + "import io.github.muehmar.pojoextension.annotations.PojoExtension;\n"
            + "@PojoExtension\n"
            + "public class "
            + className
            + "<T extends List<String> & Comparable<T>> {\n"
            + "  private final String prop1;\n"
            + "  private final T data;\n"
            + "\n"
            + "  public "
            + className
            + "(String prop1, T data) {\n"
            + "    this.prop1 = prop1;\n"
            + "    this.data = data;\n"
            + "  }\n"
            + "}";

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PojoField f1 = new PojoField(Name.fromString("prop1"), string(), REQUIRED);
    final PojoField f2 =
        new PojoField(Name.fromString("data"), Type.typeVariable(Name.fromString("T")), REQUIRED);
    final PList<PojoField> fields = PList.of(f1, f2);

    final Generic generic =
        new Generic(
            Name.fromString("T"),
            PList.of(
                Type.list(string()), Type.comparable(Type.typeVariable(Name.fromString("T")))));

    final Pojo expected =
        PojoBuilder.create()
            .name(className)
            .pkg(PACKAGE)
            .fields(fields)
            .constructors(
                PList.single(new Constructor(className, fields.map(PojoFields::toArgument))))
            .getters(PList.empty())
            .generics(PList.single(generic))
            .build();

    assertEquals(expected, pojoAndSettings.getPojo());
  }
}
