package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.WRAP_INTO_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Names;
import io.github.muehmar.pojoextension.generator.data.type.Types;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConstructorTest {
  @Test
  void matchFields_when_sameOrderAndSameTypes_then_doesMatchAndReturnSameFields() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Names.id(), Types.string()),
                new Argument(Names.zip(), Types.integer())));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Names.id(), Types.string(), REQUIRED),
            new PojoField(Names.zip(), Types.integer(), OPTIONAL));

    // method call
    final Optional<PList<FieldArgument>> result = constructor.matchFields(fields);

    final PList<FieldArgument> expected =
        fields
            .zip(constructor.getArguments())
            .map(p -> new FieldArgument(p.first(), p.second(), SAME_TYPE));

    assertEquals(Optional.of(expected), result);
  }

  @Test
  void matchFields_when_sameTypesInWrongOrder_then_doesNotMatch() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Names.id(), Types.string()),
                new Argument(Names.zip(), Types.integer())));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Names.zip(), Types.integer(), REQUIRED),
            new PojoField(Names.id(), Types.string(), OPTIONAL));

    // method call
    final Optional<PList<FieldArgument>> result = constructor.matchFields(fields);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void
      matchFields_when_sameTypesButOptionalFieldIsWrappedInOptional_then_doesMatchAndReturnSameFields() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Names.id(), Types.string()),
                new Argument(Names.zip(), Types.optional(Types.integer()))));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Names.id(), Types.string(), REQUIRED),
            new PojoField(Names.zip(), Types.integer(), OPTIONAL));

    // method call
    final Optional<PList<FieldArgument>> result = constructor.matchFields(fields);

    final PList<FieldArgument> expected =
        fields
            .zip(constructor.getArguments())
            .map(
                p ->
                    new FieldArgument(
                        p.first(),
                        p.second(),
                        p.first().isRequired() ? SAME_TYPE : WRAP_INTO_OPTIONAL));

    assertEquals(Optional.of(expected), result);
  }

  @Test
  void matchFields_when_sameTypesButRequiredFieldIsWrappedInOptional_then_doesNotMatch() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Names.id(), Types.optional(Types.string())),
                new Argument(Names.zip(), Types.integer())));

    final PList<PojoField> fields =
        PList.of(
            new PojoField(Names.id(), Types.string(), REQUIRED),
            new PojoField(Names.zip(), Types.integer(), OPTIONAL));

    // method call
    final Optional<PList<FieldArgument>> result = constructor.matchFields(fields);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void matchFields_when_fieldsAndArgumentsHaveNotTheSameSize_then_doesNotMatch() {
    final Constructor constructor =
        new Constructor(
            Name.fromString("Customer"),
            PList.of(
                new Argument(Names.id(), Types.string()),
                new Argument(Names.zip(), Types.optional(Types.integer()))));

    final PList<PojoField> fields = PList.of(new PojoField(Names.id(), Types.string(), REQUIRED));

    // method call
    final Optional<PList<FieldArgument>> result = constructor.matchFields(fields);

    assertEquals(Optional.empty(), result);
  }
}
