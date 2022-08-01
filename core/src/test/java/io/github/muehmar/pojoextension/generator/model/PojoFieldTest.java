package io.github.muehmar.pojoextension.generator.model;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.FieldBuilderMethods;
import io.github.muehmar.pojoextension.exception.PojoExtensionException;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import org.junit.jupiter.api.Test;

class PojoFieldTest {

  @Test
  void builderSetMethodName_when_noPrefix_then_isFieldName() {
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(empty());
    final Name builderSetMethodName = PojoFields.requiredId().builderSetMethodName(settings);

    assertEquals("id", builderSetMethodName.asString());
  }

  @Test
  void builderSetMethodName_when_with_then_correctPrefixed() {
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set"));
    final Name builderSetMethodName = PojoFields.requiredId().builderSetMethodName(settings);

    assertEquals("setId", builderSetMethodName.asString());
  }

  @Test
  void isFieldBuilderMethod_when_everythingOk_then_true() {
    final PojoField field = PojoFields.requiredId();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            field, Name.fromString("method"), Argument.of(Name.fromString("val"), Types.string()));

    assertTrue(field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }

  @Test
  void isFieldBuilderMethod_when_fieldNameDoesNotMatch_then_false() {
    final PojoField field = PojoFields.requiredId();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                field,
                Name.fromString("method"),
                Argument.of(Name.fromString("val"), Types.string()))
            .withFieldName(Name.fromString("anyOtherName"));

    assertFalse(field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }

  @Test
  void
      isFieldBuilderMethod_when_fieldNameDoesNotMatchAndReturnTypeDoesNotMatch_then_falseAndDoNotThrow() {
    final PojoField field = PojoFields.requiredId();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                field,
                Name.fromString("method"),
                Argument.of(Name.fromString("val"), Types.string()))
            .withFieldName(Name.fromString("anyOtherName"))
            .withReturnType(Types.voidType());

    assertFalse(field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }

  @Test
  void isFieldBuilderMethod_when_returnTypeDoesNotMatchForRequiredField_then_throwException() {
    final PojoField field = PojoFields.requiredId();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                field,
                Name.fromString("method"),
                Argument.of(Name.fromString("val"), Types.string()))
            .withReturnType(Types.string());

    assertThrows(
        PojoExtensionException.class,
        () -> field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }

  @Test
  void isFieldBuilderMethod_when_optionalReturnTypeForRequiredField_then_throwException() {
    final PojoField field = PojoFields.requiredId();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                field,
                Name.fromString("method"),
                Argument.of(Name.fromString("val"), Types.string()))
            .withReturnType(Types.optional(field.getType()));

    assertThrows(
        PojoExtensionException.class,
        () -> field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }

  @Test
  void isFieldBuilderMethod_when_returnTypeDoesNotMatchForOptionalField_then_throwException() {
    final PojoField field = PojoFields.optionalName();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                field,
                Name.fromString("method"),
                Argument.of(Name.fromString("val"), Types.string()))
            .withReturnType(Types.integer());

    assertThrows(
        PojoExtensionException.class,
        () -> field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }

  @Test
  void isFieldBuilderMethod_when_returnTypeIsOptionalOfOptionalField_then_true() {
    final PojoField field = PojoFields.optionalName();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
                field,
                Name.fromString("method"),
                Argument.of(Name.fromString("val"), Types.string()))
            .withReturnType(Types.optional(field.getType()));

    assertTrue(field.isFieldBuilderMethod(Name.fromString("Class"), fieldBuilderMethod));
  }
}
