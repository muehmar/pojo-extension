package io.github.muehmar.pojoextension.generator.model;

import static io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.WRAP_INTO_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.bluecare.commons.data.PList;
import java.util.EnumMap;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class OptionalFinalConstructorArgumentRelationTest {
  @ParameterizedTest
  @EnumSource(OptionalFieldRelation.class)
  void getTransitionMap_when_calledWithEachEnum_then_shouldReturnATransitionForEeachEnum(
      OptionalFieldRelation relation) {
    final EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> transitionMap =
        relation.getTransitionMap();

    PList.fromArray(OptionalFieldRelation.values())
        .forEach(next -> assertNotNull(transitionMap.get(next)));
  }

  @Test
  void andThen_when_calledForUnwrapOptional_then_correctResultingTransitions() {
    final OptionalFieldRelation current = UNWRAP_OPTIONAL;
    assertEquals(SAME_TYPE, current.andThen(WRAP_INTO_OPTIONAL));
    assertEquals(UNWRAP_OPTIONAL, current.andThen(SAME_TYPE));
    assertThrows(IllegalStateException.class, () -> current.andThen(UNWRAP_OPTIONAL));
  }

  @Test
  void andThen_when_calledForSameType_then_correctResultingTransitions() {
    final OptionalFieldRelation current = SAME_TYPE;
    assertEquals(WRAP_INTO_OPTIONAL, current.andThen(WRAP_INTO_OPTIONAL));
    assertEquals(UNWRAP_OPTIONAL, current.andThen(UNWRAP_OPTIONAL));
    assertEquals(SAME_TYPE, current.andThen(SAME_TYPE));
  }

  @Test
  void andThen_when_calledForWrapIntoOptional_then_correctResultingTransitions() {
    final OptionalFieldRelation current = WRAP_INTO_OPTIONAL;
    assertEquals(SAME_TYPE, current.andThen(UNWRAP_OPTIONAL));
    assertEquals(WRAP_INTO_OPTIONAL, current.andThen(SAME_TYPE));
    assertThrows(IllegalStateException.class, () -> current.andThen(WRAP_INTO_OPTIONAL));
  }
}
