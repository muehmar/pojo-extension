package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GetterTest {

  @Test
  void getterName_when_calledWithName_then_correctGetterName() {
    final Name getterName = Getter.getterName(Name.fromString("id"));
    assertEquals("getId", getterName.asString());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getFieldGetter_when_nameAndTypeMatches_then_returnsFieldGetter(boolean required) {
    final Getter getter = new Getter(Name.fromString("getId"), Type.string());
    final PojoField field = new PojoField(Type.string(), Name.fromString("id"), required);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, SAME_TYPE);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getFieldGetter_when_nameDoesNotMatch_then_returnsEmpty(boolean required) {
    final Getter getter = new Getter(Name.fromString("getName"), Type.string());
    final PojoField field = new PojoField(Type.string(), Name.fromString("id"), required);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void getFieldGetter_when_returnTypeWrappedInOptionalAndFieldRequired_then_returnsEmpty() {
    final Getter getter = new Getter(Name.fromString("getId"), Type.optional(Type.string()));
    final PojoField field = new PojoField(Type.string(), Name.fromString("id"), true);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void
      getFieldGetter_when_returnTypeWrappedInOptionalAndFieldNotRequired_then_returnsFieldGetter() {
    final Getter getter = new Getter(Name.fromString("getId"), Type.optional(Type.string()));
    final PojoField field = new PojoField(Type.string(), Name.fromString("id"), false);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    final FieldGetter expected = new FieldGetter(getter, field, UNWRAP_OPTIONAL);

    assertEquals(Optional.of(expected), fieldGetter);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getFieldGetter_when_typeDoesNotMatch_then_returnsEmpty(boolean required) {
    final Getter getter = new Getter(Name.fromString("getId"), Type.integer());
    final PojoField field = new PojoField(Type.string(), Name.fromString("id"), required);

    final Optional<FieldGetter> fieldGetter = getter.getFieldGetter(field);

    assertEquals(Optional.empty(), fieldGetter);
  }
}
