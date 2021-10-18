package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.Type.optional;
import static io.github.muehmar.pojoextension.generator.data.Type.string;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArgumentTest {

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void matchesType_when_typesAreTheSame_then_typeMatches(boolean fieldRequired) {
    final Argument argument = new Argument(Name.fromString("id"), string());
    final PojoField field = new PojoField(string(), Name.fromString("extId"), fieldRequired);

    assertTrue(argument.matchesType(field));
  }

  @Test
  void matchesType_when_sameTypeWrappedInOptionalForArgumentAndFieldNotRequired_then_typeMatches() {
    final Argument argument = new Argument(Name.fromString("id"), optional(string()));
    final PojoField field = new PojoField(string(), Name.fromString("extId"), false);

    assertTrue(argument.matchesType(field));
  }

  @Test
  void
      matchesType_when_sameTypeWrappedInOptionalForArgumentAndFieldRequired_then_typeDoesNotMatch() {
    final Argument argument = new Argument(Name.fromString("id"), optional(string()));
    final PojoField field = new PojoField(string(), Name.fromString("extId"), true);

    assertFalse(argument.matchesType(field));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void matchesType_when_sameTypeWrappedInOptionalForField_then_typeDoesNotMatch(
      boolean fieldRequired) {
    final Argument argument = new Argument(Name.fromString("id"), string());
    final PojoField field =
        new PojoField(optional(string()), Name.fromString("extId"), fieldRequired);

    assertFalse(argument.matchesType(field));
  }
}
