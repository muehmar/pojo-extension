package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConstructorTest {
  @Test
  void matchFields_when_sameOrderAndSameTypes_then_doesMatchAndReturnSameFields() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Name.fromString("id"), Type.string()),
                new Argument(Name.fromString("zip"), Type.integer())));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Type.string(), Name.fromString("id"), true),
            new PojoField(Type.integer(), Name.fromString("zip"), false));

    final Optional<PList<PojoField>> result = constructor.matchFields(fields);

    assertEquals(Optional.of(fields), result);
  }

  @Test
  void matchFields_when_sameTypesInWrongOrder_then_doesNotMatch() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Name.fromString("id"), Type.string()),
                new Argument(Name.fromString("zip"), Type.integer())));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Type.integer(), Name.fromString("zip"), false),
            new PojoField(Type.string(), Name.fromString("id"), true));

    final Optional<PList<PojoField>> result = constructor.matchFields(fields);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void
      matchFields_when_sameTypesButOptionalFieldIsWrappedInOptional_then_doesMatchAndReturnSameFields() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Name.fromString("id"), Type.string()),
                new Argument(Name.fromString("zip"), Type.optional(Type.integer()))));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Type.string(), Name.fromString("id"), true),
            new PojoField(Type.integer(), Name.fromString("zip"), false));

    final Optional<PList<PojoField>> result = constructor.matchFields(fields);

    assertEquals(Optional.of(fields), result);
  }

  @Test
  void matchFields_when_fieldsAndArgumentsHaveNotTheSameSize_then_doesNotMatch() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Name.fromString("id"), Type.string()),
                new Argument(Name.fromString("zip"), Type.optional(Type.integer()))));

    final PList<PojoField> fields =
        PList.of(new PojoField(Type.string(), Name.fromString("id"), true));

    final Optional<PList<PojoField>> result = constructor.matchFields(fields);

    assertEquals(Optional.empty(), result);
  }
}
