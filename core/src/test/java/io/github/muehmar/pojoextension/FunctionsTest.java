package io.github.muehmar.pojoextension;

import static io.github.muehmar.pojoextension.Functions.mapFirst;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class FunctionsTest {

  @Test
  void mapFirst_when_calledForList_then_firstElementMapped() {
    final PList<String> result =
        PList.of("one", "two", "three").zipWithIndex().map(mapFirst(str -> str + "-mapped"));

    assertEquals(PList.of("one-mapped", "two", "three"), result);
  }
}
