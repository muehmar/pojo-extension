package io.github.muehmar.pojoextension.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethodBuilder;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PojoExtensionProcessorFieldBuilderTest extends BaseExtensionProcessorTest {

  @Test
  void run_when_fieldBuilderAnnotationOnMethod_then_extractFieldBuilderMethod() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(FieldBuilder.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("Integer", "key")
            .constructor()
            .getter("Integer", "key")
            .methodWithAnnotation(
                "static Integer",
                "sumKey",
                "return Integer.parseInt(a) + b",
                "@FieldBuilder(fieldName=\"key\")",
                "String a, int b")
            .create();

    final BaseExtensionProcessorTest.PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final NonEmptyList<FieldBuilderMethod> methods =
        NonEmptyList.of(
            FieldBuilderMethodBuilder.create()
                .fieldName(Name.fromString("key"))
                .methodName(Name.fromString("sumKey"))
                .returnType(Types.integer())
                .arguments(
                    PList.of(
                        new Argument(Name.fromString("a"), Types.string()),
                        new Argument(Name.fromString("b"), Types.primitiveInt())))
                .andAllOptionals()
                .innerClassName(Optional.empty())
                .build());

    final io.github.muehmar.pojoextension.generator.model.FieldBuilder fieldBuilder =
        new io.github.muehmar.pojoextension.generator.model.FieldBuilder(false, methods);

    assertEquals(PList.single(fieldBuilder), pojoAndSettings.getPojo().getFieldBuilders());
  }

  @Test
  void run_when_fieldBuilderAnnotationOnClass_then_extractFieldBuilderMethod() {
    final Name className = randomClassName();

    final String classString =
        "package "
            + PACKAGE
            + ";\n"
            + "import io.github.muehmar.pojoextension.annotations.PojoExtension;\n"
            + "import io.github.muehmar.pojoextension.annotations.FieldBuilder;\n"
            + "@PojoExtension\n"
            + "public class "
            + className
            + " {\n"
            + "  private final String id;\n"
            + "  public "
            + className
            + "(String id) {\n"
            + "    this.id = id;\n"
            + "  }\n"
            + "\n"
            + "  @FieldBuilder(fieldName = \"id\")\n"
            + "  static class CustomIdBuilder {\n"
            + "    static String randomId(int seed) {\n"
            + "      return \"random\" + seed;\n"
            + "    } \n"
            + "    static String constant() {\n"
            + "      return \"constant\";\n"
            + "    } \n"
            + "    static String varargs(String... args) {\n"
            + "      return String.join(\"\", args);\n"
            + "    } \n"
            + "  }\n"
            + "}";

    final BaseExtensionProcessorTest.PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PList<FieldBuilderMethod> methods =
        PList.of(
            FieldBuilderMethodBuilder.create()
                .fieldName(Name.fromString("id"))
                .methodName(Name.fromString("randomId"))
                .returnType(Types.string())
                .arguments(PList.of(new Argument(Name.fromString("seed"), Types.primitiveInt())))
                .andAllOptionals()
                .innerClassName(Optional.of(Name.fromString("CustomIdBuilder")))
                .build(),
            FieldBuilderMethodBuilder.create()
                .fieldName(Name.fromString("id"))
                .methodName(Name.fromString("constant"))
                .returnType(Types.string())
                .arguments(PList.empty())
                .andAllOptionals()
                .innerClassName(Optional.of(Name.fromString("CustomIdBuilder")))
                .build(),
            FieldBuilderMethodBuilder.create()
                .fieldName(Name.fromString("id"))
                .methodName(Name.fromString("varargs"))
                .returnType(Types.string())
                .arguments(
                    PList.single(
                        new Argument(Name.fromString("args"), Types.varargs(Types.string()))))
                .andAllOptionals()
                .innerClassName(Optional.of(Name.fromString("CustomIdBuilder")))
                .build());

    assertEquals(1, pojoAndSettings.getPojo().getFieldBuilders().size());
    assertEquals(
        methods.toHashSet(),
        pojoAndSettings.getPojo().getFieldBuilders().apply(0).getMethods().toPList().toHashSet());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void run_when_fieldBuilderAnnotationOnMethod_then_extractTheDisableMethodsFlagCorrectly(
      boolean disableDefaultMethods) {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(FieldBuilder.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("Integer", "key")
            .constructor()
            .getter("Integer", "key")
            .methodWithAnnotation(
                "static Integer",
                "sumKey",
                "return Integer.parseInt(a) + b",
                "@FieldBuilder(fieldName=\"key\", disableDefaultMethods="
                    + disableDefaultMethods
                    + ")",
                "String a, int b")
            .create();

    final BaseExtensionProcessorTest.PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PList<io.github.muehmar.pojoextension.generator.model.FieldBuilder> fieldBuilders =
        pojoAndSettings.getPojo().getFieldBuilders();

    assertEquals(1, fieldBuilders.size());
    final io.github.muehmar.pojoextension.generator.model.FieldBuilder fieldBuilder =
        fieldBuilders.apply(0);

    assertEquals(disableDefaultMethods, fieldBuilder.isDisableDefaultMethods());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void run_when_fieldBuilderAnnotationOnClass_then_extractTheDisableMethodsFlagCorrectly(
      boolean disableDefaultMethods) {
    final Name className = randomClassName();

    final String classString =
        "package "
            + PACKAGE
            + ";\n"
            + "import io.github.muehmar.pojoextension.annotations.PojoExtension;\n"
            + "import io.github.muehmar.pojoextension.annotations.FieldBuilder;\n"
            + "@PojoExtension\n"
            + "public class "
            + className
            + " {\n"
            + "  private final String id;\n"
            + "  public "
            + className
            + "(String id) {\n"
            + "    this.id = id;\n"
            + "  }\n"
            + "\n"
            + "  @FieldBuilder(fieldName = \"id\", disableDefaultMethods="
            + disableDefaultMethods
            + ")\n"
            + "  static class CustomIdBuilder {\n"
            + "    static String randomId(int seed) {\n"
            + "      return \"random\" + seed;\n"
            + "    } \n"
            + "    static String constant() {\n"
            + "      return \"constant\";\n"
            + "    } \n"
            + "    static String varargs(String... args) {\n"
            + "      return String.join(\"\", args);\n"
            + "    } \n"
            + "  }\n"
            + "}";

    final BaseExtensionProcessorTest.PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final PList<io.github.muehmar.pojoextension.generator.model.FieldBuilder> fieldBuilders =
        pojoAndSettings.getPojo().getFieldBuilders();

    assertEquals(1, fieldBuilders.size());
    final io.github.muehmar.pojoextension.generator.model.FieldBuilder fieldBuilder =
        fieldBuilders.apply(0);

    assertEquals(disableDefaultMethods, fieldBuilder.isDisableDefaultMethods());
  }
}
