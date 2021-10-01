package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NameTest {
  @Test
  void toPascalCase_when_valueStartWithLowercase_then_outputStartsWithUppercase() {
    final Name name = Name.fromString("name");
    assertEquals("Name", name.toPascalCase().asString());
  }
}
