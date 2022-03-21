package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.NonEmptyList;
import io.github.muehmar.pojoextension.FieldBuilderMethods;
import io.github.muehmar.pojoextension.generator.BuilderFields;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.FieldBuilder;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BuilderFieldTest {

  @Test
  void hasFieldBuilder_when_noBuilderPresent_then_false() {
    final BuilderField builderField =
        BuilderFields.of(Pojos.sample(), Pojos.sample().getFields().apply(0), 0)
            .withFieldBuilder(empty());

    assertFalse(builderField.hasFieldBuilder());
  }

  @Test
  void hasFieldBuilder_when_builderPresent_then_true() {
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            Pojos.sample().getFields().apply(0),
            Name.fromString("method"),
            new Argument(Name.fromString("arg"), Types.string()));

    final FieldBuilder fieldBuilder = new FieldBuilder(false, NonEmptyList.of(fieldBuilderMethod));

    final BuilderField builderField =
        BuilderFields.of(Pojos.sample(), Pojos.sample().getFields().apply(0), 0)
            .withFieldBuilder(fieldBuilder);

    assertTrue(builderField.hasFieldBuilder());
  }

  @Test
  void isDisableDefaultMethods_when_noBuilderPresent_then_false() {
    final BuilderField builderField =
        BuilderFields.of(Pojos.sample(), Pojos.sample().getFields().apply(0), 0)
            .withFieldBuilder(empty());

    assertFalse(builderField.isDisableDefaultMethods());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void isDisableDefaultMethods_when_builderPresent_then_returnValueFromFieldBuilder(
      boolean disableDefaultMethods) {
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            Pojos.sample().getFields().apply(0),
            Name.fromString("method"),
            new Argument(Name.fromString("arg"), Types.string()));

    final FieldBuilder fieldBuilder =
        new FieldBuilder(disableDefaultMethods, NonEmptyList.of(fieldBuilderMethod));

    final BuilderField builderField =
        BuilderFields.of(Pojos.sample(), Pojos.sample().getFields().apply(0), 0)
            .withFieldBuilder(fieldBuilder);

    assertEquals(disableDefaultMethods, builderField.isDisableDefaultMethods());
  }

  @Test
  void isFieldOptional_when_requiredField_then_false() {
    final BuilderField builderField =
        BuilderFields.of(Pojos.sample(), PojoFields.requiredMap(), 0).withFieldBuilder(empty());

    assertFalse(builderField.isFieldOptional());
  }

  @Test
  void isFieldOptional_when_optionalField_then_true() {
    final BuilderField builderField =
        BuilderFields.of(Pojos.sample(), PojoFields.optionalName(), 0).withFieldBuilder(empty());

    assertTrue(builderField.isFieldOptional());
  }
}
