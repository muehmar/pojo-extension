package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PojoFieldTest {

  @Test
  void forArgument_when_typesMatchExactly_then_exactMatchExecuted() {
    final Type type = Type.string();

    final PojoField field =
        PojoField.newBuilder()
            .setType(type)
            .setName(Name.fromString("user"))
            .setRequired(false)
            .andAllOptionals()
            .build();

    final Argument arg = Argument.of(Name.fromString("user"), type);

    final String result =
        field.forArgument(
            arg, (_1, _2) -> "exactMatch", (_1, _2) -> "optionalMatch", (_1, _2) -> "noMatch");

    assertEquals("exactMatch", result);
  }

  @Test
  void forArgument_when_argumentIsOptionalOfFieldType_then_optionalMatchExecuted() {
    final Type type = Type.string();

    final PojoField field =
        PojoField.newBuilder()
            .setType(type)
            .setName(Name.fromString("user"))
            .setRequired(false)
            .andAllOptionals()
            .build();

    final Argument arg = Argument.of(Name.fromString("user"), Type.optional(type));

    final String result =
        field.forArgument(
            arg, (_1, _2) -> "exactMatch", (_1, _2) -> "optionalMatch", (_1, _2) -> "noMatch");

    assertEquals("optionalMatch", result);
  }

  @ParameterizedTest
  @MethodSource("noMatchTypes")
  void forArgument_when_typesDoNotMatch_then_noMatchExecuted(Type type2) {
    final Type type1 = Type.string();

    final PojoField field =
        PojoField.newBuilder()
            .setType(type1)
            .setName(Name.fromString("user"))
            .setRequired(false)
            .andAllOptionals()
            .build();

    final Argument arg = Argument.of(Name.fromString("user"), type2);

    final String result =
        field.forArgument(
            arg, (_1, _2) -> "exactMatch", (_1, _2) -> "optionalMatch", (_1, _2) -> "noMatch");

    assertEquals("noMatch", result);
  }

  private static Stream<Arguments> noMatchTypes() {
    return Stream.of(Arguments.of(Type.optional(Type.integer())), Arguments.of(Type.integer()));
  }
}
