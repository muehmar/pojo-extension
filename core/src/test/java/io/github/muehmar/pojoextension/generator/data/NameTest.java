package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NameTest {
  @Test
  void toPascalCase_when_valueStartWithLowercase_then_outputStartsWithUppercase() {
    final Name name = Name.fromString("name");
    assertEquals("Name", name.toPascalCase().asString());
  }

  @Test
  void javaBeansName_when_called_then_correctJavaBeansName() {
    assertEquals("Name", Name.fromString("name").javaBeansName().asString());
    assertEquals("xIndex", Name.fromString("xIndex").javaBeansName().asString());
    assertEquals("XxIndex", Name.fromString("xxIndex").javaBeansName().asString());
  }

  @Test
  void replace_when_called_then_oldNameReplaceWithNew() {
    final Name name = Name.fromString("HelloWorld!");
    assertEquals(
        "HelloReplace!",
        name.replace(Name.fromString("World"), Name.fromString("Replace")).asString());
  }
}
