package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class AllRequiredClassTest {

  @Test
  void newBuilder_when_used_then_noOptionalFieldsPresent() {
    final AllRequiredClass allRequiredClass =
        AllRequiredClassBuilder.create()
            .setId("id")
            .setFlag(Optional.of(true))
            .setAge(30)
            .andAllOptionals()
            .build();

    assertEquals("id", allRequiredClass.getId());
    assertEquals(Optional.of(true), allRequiredClass.getFlag());
    assertEquals(30, allRequiredClass.getAge());
  }
}
