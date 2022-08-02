package io.github.muehmar.pojoextension.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.BuildMethod;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PojoExtensionProcessorBuildMethodTest extends BaseExtensionProcessorTest {
  @Test
  void run_when_simplePojoWithoutBuildMethod_then_noBuildMethodInPojo() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("String", "id")
            .constructor()
            .create();

    final BaseExtensionProcessorTest.PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final Optional<BuildMethod> buildMethod = pojoAndSettings.getPojo().getBuildMethod();
    assertEquals(Optional.empty(), buildMethod);
  }

  @Test
  void run_when_simplePojoWithCorrectBuildMethod_then_correctBuildMethodInPojo() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(io.github.muehmar.pojoextension.annotations.BuildMethod.class)
            .annotation(PojoExtension.class)
            .className(className)
            .withField("String", "id")
            .constructor()
            .getter("String", "id")
            .methodWithAnnotation(
                "static String",
                "customBuildMethod",
                "return inst.toString()",
                "@BuildMethod",
                className + " inst")
            .create();

    final BaseExtensionProcessorTest.PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    final Optional<BuildMethod> buildMethod = pojoAndSettings.getPojo().getBuildMethod();
    final BuildMethod expected =
        new BuildMethod(Name.fromString("customBuildMethod"), Types.string());
    assertEquals(Optional.of(expected), buildMethod);
  }
}
