package io.github.muehmar.pojoextension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MapperTest {
  @Test
  void map_when_called_then_mappingFunctionApplied() {
    final int result = Mapper.initial(1).map(i -> i + 4).apply();
    assertEquals(5, result);
  }

  @Test
  void mapConditionally_when_conditionFalse_then_mappingFunctionNotApplied() {
    final int result = Mapper.initial(1).mapConditionally(false, i -> i + 4).apply();
    assertEquals(1, result);
  }

  @Test
  void mapConditionally_when_conditionTrue_then_mappingFunctionApplied() {
    final int result = Mapper.initial(1).mapConditionally(true, i -> i + 4).apply();
    assertEquals(5, result);
  }
}
