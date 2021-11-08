package io.github.muehmar.pojoextension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BooleansTest {
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void not_when_booleanAsInput_then_negated(boolean in) {
    assertEquals(!in, Booleans.not(in));
  }
}
