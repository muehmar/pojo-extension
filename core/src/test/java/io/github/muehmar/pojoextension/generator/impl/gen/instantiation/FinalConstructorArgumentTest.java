package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.WRAP_INTO_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.FieldArgument;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;
import org.junit.jupiter.api.Test;

class FinalConstructorArgumentTest {
  @Test
  void
      ofGetter_when_getterReturnsSameTypeAndConstructorArgumentIsWrappedIntoOptional_then_correctResult() {
    final Name name = Name.fromString("id");
    final PojoField pojoField = new PojoField(Type.string(), name, false);
    final FieldGetter fieldGetter =
        FieldGetter.of(
            new Getter(Getter.getterName(pojoField), Type.string()), pojoField, SAME_TYPE);
    final FieldArgument fieldArgument =
        new FieldArgument(
            pojoField, new Argument(name, Type.optional(Type.string())), WRAP_INTO_OPTIONAL);

    // method call
    final FinalConstructorArgument finalConstructorArgument =
        FinalConstructorArgument.ofGetter(fieldGetter, fieldArgument);

    assertEquals("self.getId()", finalConstructorArgument.getFieldString());
    assertEquals(WRAP_INTO_OPTIONAL, finalConstructorArgument.getRelation());
  }

  @Test
  void
      ofGetter_when_getterReturnsWrappedIntoOptionalAndConstructorArgumentIsSameType_then_correctResult() {
    final Name name = Name.fromString("id");
    final PojoField pojoField = new PojoField(Type.string(), name, false);
    final FieldGetter fieldGetter =
        FieldGetter.of(
            new Getter(Getter.getterName(pojoField), Type.string()), pojoField, UNWRAP_OPTIONAL);
    final FieldArgument fieldArgument =
        new FieldArgument(pojoField, new Argument(name, Type.optional(Type.string())), SAME_TYPE);

    // method call
    final FinalConstructorArgument finalConstructorArgument =
        FinalConstructorArgument.ofGetter(fieldGetter, fieldArgument);

    assertEquals("self.getId()", finalConstructorArgument.getFieldString());
    assertEquals(UNWRAP_OPTIONAL, finalConstructorArgument.getRelation());
  }

  @Test
  void
      ofGetter_when_getterReturnsWrappedIntoOptionalAndConstructorArgumentIsWrappedIntoOptional_then_correctResult() {
    final Name name = Name.fromString("id");
    final PojoField pojoField = new PojoField(Type.string(), name, false);
    final FieldGetter fieldGetter =
        FieldGetter.of(
            new Getter(Getter.getterName(pojoField), Type.string()), pojoField, UNWRAP_OPTIONAL);
    final FieldArgument fieldArgument =
        new FieldArgument(
            pojoField, new Argument(name, Type.optional(Type.string())), WRAP_INTO_OPTIONAL);

    // method call
    final FinalConstructorArgument finalConstructorArgument =
        FinalConstructorArgument.ofGetter(fieldGetter, fieldArgument);

    assertEquals("self.getId()", finalConstructorArgument.getFieldString());
    assertEquals(SAME_TYPE, finalConstructorArgument.getRelation());
  }

  @Test
  void
      ofFieldVariable_when_variableIsWrappedIntoOptionalAndArgumentIsWrappedIntoOptional_then_correctResult() {
    final Name name = Name.fromString("id");
    final PojoField pojoField = new PojoField(Type.string(), name, false);
    final FieldVariable fieldVariable =
        new FieldVariable(Pojos.sample(), pojoField, UNWRAP_OPTIONAL);
    final FieldArgument fieldArgument =
        new FieldArgument(
            pojoField, new Argument(name, Type.optional(Type.string())), WRAP_INTO_OPTIONAL);

    // method call
    final FinalConstructorArgument finalConstructorArgument =
        FinalConstructorArgument.ofFieldVariable(fieldVariable, fieldArgument);

    assertEquals("id", finalConstructorArgument.getFieldString());
    assertEquals(SAME_TYPE, finalConstructorArgument.getRelation());
  }

  @Test
  void
      ofFieldVariable_when_variableIsSameTypeAndArgumentIsWrappedIntoOptional_then_correctResult() {
    final Name name = Name.fromString("id");
    final PojoField pojoField = new PojoField(Type.string(), name, false);
    final FieldVariable fieldVariable = new FieldVariable(Pojos.sample(), pojoField, SAME_TYPE);
    final FieldArgument fieldArgument =
        new FieldArgument(
            pojoField, new Argument(name, Type.optional(Type.string())), WRAP_INTO_OPTIONAL);

    // method call
    final FinalConstructorArgument finalConstructorArgument =
        FinalConstructorArgument.ofFieldVariable(fieldVariable, fieldArgument);

    assertEquals("id", finalConstructorArgument.getFieldString());
    assertEquals(WRAP_INTO_OPTIONAL, finalConstructorArgument.getRelation());
  }

  @Test
  void ofFieldVariable_when_variableIsSameTypeAndArgumentIsSameType_then_correctResult() {
    final Name name = Name.fromString("id");
    final PojoField pojoField = new PojoField(Type.string(), name, false);
    final FieldVariable fieldVariable =
        new FieldVariable(Pojos.sample(), pojoField, UNWRAP_OPTIONAL);
    final FieldArgument fieldArgument =
        new FieldArgument(pojoField, new Argument(name, Type.optional(Type.string())), SAME_TYPE);

    // method call
    final FinalConstructorArgument finalConstructorArgument =
        FinalConstructorArgument.ofFieldVariable(fieldVariable, fieldArgument);

    assertEquals("id", finalConstructorArgument.getFieldString());
    assertEquals(UNWRAP_OPTIONAL, finalConstructorArgument.getRelation());
  }
}
