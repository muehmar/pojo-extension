package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class GetterTest {

  @ParameterizedTest
  @MethodSource("getterNames")
  void getterName_when_calledWithField_then_correctJavaBeansName(
      String fieldName, Type type, String expectedGetterName) {
    final PojoField field = new PojoField(Name.fromString(fieldName), type, true);
    assertEquals(expectedGetterName, Getter.getterName(field).asString());
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
  @ValueSource(booleans = {true, false})
  void getFieldGetter_when_nameAndTypeMatches_then_returnsFieldGetter(boolean required) {
    final Getter getter = new Getter(Name.fromString("getId"), Type.string());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), required);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, SAME_TYPE);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getFieldGetter_when_nameDoesNotMatch_then_returnsEmpty(boolean required) {
    final Getter getter = new Getter(Name.fromString("getName"), Type.string());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), required);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void getFieldGetter_when_returnTypeWrappedInOptionalAndFieldRequired_then_returnsEmpty() {
    final Getter getter = new Getter(Name.fromString("getId"), Type.optional(Type.string()));
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), true);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void
      getFieldGetter_when_returnTypeWrappedInOptionalAndFieldNotRequired_then_returnsFieldGetter() {
    final Getter getter = new Getter(Name.fromString("getId"), Type.optional(Type.string()));
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), false);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, UNWRAP_OPTIONAL);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getFieldGetter_when_typeDoesNotMatch_then_returnsEmpty(boolean required) {
    final Getter getter = new Getter(Name.fromString("getId"), Type.integer());
    final PojoField field = new PojoField(Name.fromString("id"), Type.string(), required);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }
}
