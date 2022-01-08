package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class SampleRecordTest {
  @Test
  void create_when_populateBuilder_then_correctInstanceCreated() {
    final SampleRecord sampleRecord =
        SampleRecordBuilder.create()
            .id(12L)
            .name("name")
            .andAllOptionals()
            .data("Hello World!")
            .build();

    assertEquals(12L, sampleRecord.id());
    assertEquals("name", sampleRecord.name());
    assertEquals(Optional.of("Hello World!"), sampleRecord.data());
  }
}
