package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.Getter.noFieldName;
import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class GetterTest {

  @ParameterizedTest
  @MethodSource("getterNames")
  void javaBeanGetterName_when_calledWithField_then_correctJavaBeansName(
      String fieldName, Type type, String expectedGetterName) {
    final PojoField field = new PojoField(Name.fromString(fieldName), type, REQUIRED);
    assertEquals(expectedGetterName, Getter.javaBeanGetterName(field).asString());
  }

  private static Stream<Arguments> getterNames() {
    return Stream.of(
        Arguments.of("id", Type.string(), "getId"),
        Arguments.of("flag", Type.primitiveBoolean(), "isFlag"),
        Arguments.of("xIndex", Type.integer(), "getxIndex"),
        Arguments.of("isFlag", Type.primitiveBoolean(), "isFlag"),
        Arguments.of("istio", Type.primitiveBoolean(), "isIstio"),
        Arguments.of("flag", Type.booleanClass(), "getFlag"));
  }

  @ParameterizedTest
  @EnumSource(Necessity.class)
  void getFieldGetter_when_getterNameAndTypeMatches_then_returnsFieldGetter(Necessity necessity) {
    final Getter getter = new Getter(Name.fromString("getId"), Type.string(), noFieldName());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), necessity);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, SAME_TYPE);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @EnumSource(Necessity.class)
  void getFieldGetter_when_fieldNameAndTypeMatches_then_returnsFieldGetter(Necessity necessity) {
    final Getter getter = new Getter(Name.fromString("id"), Type.string(), noFieldName());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), necessity);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, SAME_TYPE);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @EnumSource(Necessity.class)
  void getFieldGetter_when_nameDoesNotMatch_then_returnsEmpty(Necessity necessity) {
    final Getter getter = new Getter(Name.fromString("getName"), Type.string(), noFieldName());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), necessity);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @ParameterizedTest
  @EnumSource(Necessity.class)
  void getFieldGetter_when_nameDoesNotMatchButFieldNameMatches_then_returnsFieldGetter(
      Necessity necessity) {
    final Getter getter =
        new Getter(
            Name.fromString("getIdentification"),
            Type.string(),
            Optional.of(Name.fromString("id")));
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), necessity);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, SAME_TYPE);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @Test
  void getFieldGetter_optionalFieldNameDoesNotMatchButFieldNameMatches_then_returnFieldGetter() {
    final Getter getter =
        new Getter(
            Name.fromString("getIdentification"),
            Type.optional(Type.string()),
            Optional.of(Name.fromString("id")));
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), OPTIONAL);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, UNWRAP_OPTIONAL);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @Test
  void getFieldGetter_fieldNameMatchesButReturnTypeDoesNotForRequiredField_then_returnsEmpty() {
    final Getter getter =
        new Getter(
            Name.fromString("getIdentification"),
            Type.optional(Type.string()),
            Optional.of(Name.fromString("id")));
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), REQUIRED);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void getFieldGetter_when_returnTypeWrappedInOptionalAndFieldRequired_then_returnsEmpty() {
    final Getter getter =
        new Getter(Name.fromString("getId"), Type.optional(Type.string()), noFieldName());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), REQUIRED);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void
      getFieldGetter_when_returnTypeWrappedInOptionalAndFieldNotRequired_then_returnsFieldGetter() {
    final Getter getter =
        new Getter(Name.fromString("getId"), Type.optional(Type.string()), noFieldName());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), OPTIONAL);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, UNWRAP_OPTIONAL);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @EnumSource(Necessity.class)
  void getFieldGetter_when_typeDoesNotMatch_then_returnsEmpty(Necessity necessity) {
    final Getter getter = new Getter(Name.fromString("getId"), Type.integer(), noFieldName());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), necessity);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }
}
