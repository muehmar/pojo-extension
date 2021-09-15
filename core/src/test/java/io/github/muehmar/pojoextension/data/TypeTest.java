package io.github.muehmar.pojoextension.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TypeTest {

  @Test
  void fromQualifiedClassName_when_javaLangString_then_parsedCorrectly() {
    final Type type = Type.fromQualifiedClassName("java.lang.String");
    assertEquals(Type.string(), type);
  }
}
