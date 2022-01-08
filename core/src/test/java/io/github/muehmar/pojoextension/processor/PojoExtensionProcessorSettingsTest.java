package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class PojoExtensionProcessorSettingsTest extends BaseExtensionProcessorTest {

  @Test
  void run_when_pojoExtensionAnnotation_then_defaultSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings().withExtensionUsage(ExtensionUsage.STATIC),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_extensionNotInherited_then_extensionUsageStatic() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(ExtensionUsage.STATIC, pojoAndSettings.getSettings().getExtensionUsage());
  }

  @Test
  void run_when_extensionInherited_then_extensionUsageInherited() {
    // Use fixed class names here, see: https://github.com/jOOQ/jOOR/issues/117
    final Name className = Name.fromString("Customerf8a97e457a");
    final Name extensionClassName = className.append("Extension");

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotation(PojoExtension.class)
            .className(className.append(" extends ").append(extensionClassName))
            .create()
            .concat(String.format("class %s {}\n", extensionClassName));

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(ExtensionUsage.INHERITED, pojoAndSettings.getSettings().getExtensionUsage());
  }

  @Test
  void run_when_extendAnotherClass_then_extensionUsageStatic() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .withImport(ArrayList.class)
            .annotation(PojoExtension.class)
            .className(className.append(" extends ArrayList<String>"))
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(ExtensionUsage.STATIC, pojoAndSettings.getSettings().getExtensionUsage());
  }

  @Test
  void run_when_disabledSafeBuilder_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationBooleanParam(PojoExtension.class, "enableSafeBuilder", false)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withSafeBuilderAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_disabledEqualsAndHashCode_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationBooleanParam(PojoExtension.class, "enableEqualsAndHashCode", false)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withEqualsHashCodeAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_disabledToString_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationBooleanParam(PojoExtension.class, "enableToString", false)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withToStringAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_disabledWithers_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationBooleanParam(PojoExtension.class, "enableWithers", false)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withWithersAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_disabledMappers_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationBooleanParam(PojoExtension.class, "enableMappers", false)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withMappersAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_disableBaseClass_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationBooleanParam(PojoExtension.class, "enableBaseClass", false)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withBaseClassAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_overrideBuilderName_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationStringParam(PojoExtension.class, "builderName", "CustomBuilderName")
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withBuilderName(Name.fromString("CustomBuilderName")),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_overrideExtensionName_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationStringParam(PojoExtension.class, "extensionName", "CustomExtensionName")
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withExtensionName(Name.fromString("CustomExtensionName")),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_overrideBaseClassName_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationStringParam(PojoExtension.class, "baseClassName", "CustomerBaseClass")
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withBaseClassName(Name.fromString("CustomerBaseClass")),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_overrideOptionalDetection_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(PojoExtension.class)
            .annotationEnumParam(
                PojoExtension.class,
                "optionalDetection",
                OptionalDetection.class,
                OptionalDetection.NONE)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withOptionalDetections(PList.single(OptionalDetection.NONE)),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_safeBuilderAnnotation_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(SafeBuilder.class)
            .annotation(SafeBuilder.class)
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withWithersAbility(DISABLED)
            .withOptionalGettersAbility(DISABLED)
            .withEqualsHashCodeAbility(DISABLED)
            .withToStringAbility(DISABLED)
            .withMappersAbility(DISABLED),
        pojoAndSettings.getSettings());
  }

  @Test
  void run_when_safeBuilderAnnotationWithCustomBuilderName_then_correctSettings() {
    final Name className = randomClassName();

    final String classString =
        TestPojoComposer.ofPackage(PACKAGE)
            .withImport(SafeBuilder.class)
            .annotationStringParam(SafeBuilder.class, "builderName", "SafeBuilder")
            .className(className)
            .create();

    final PojoAndSettings pojoAndSettings =
        runAnnotationProcessor(qualifiedClassName(className), classString);

    assertEquals(
        PojoSettings.defaultSettings()
            .withExtensionUsage(ExtensionUsage.STATIC)
            .withWithersAbility(DISABLED)
            .withOptionalGettersAbility(DISABLED)
            .withEqualsHashCodeAbility(DISABLED)
            .withToStringAbility(DISABLED)
            .withMappersAbility(DISABLED)
            .withBuilderName(Name.fromString("SafeBuilder")),
        pojoAndSettings.getSettings());
  }
}
