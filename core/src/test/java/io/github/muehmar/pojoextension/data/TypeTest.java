package io.github.muehmar.pojoextension.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class TypeTest {

  @Test
  void fromQualifiedClassName_when_javaLangString_then_parsedCorrectly() {
    final Type type = Type.fromQualifiedClassName("java.lang.String");
    assertEquals(Type.string(), type);
  }

  @Test
  void fromQualifiedClassName_when_genericClass_then_parsedCorrectlyWithoutTypeParameter() {
    final Type type = Type.fromQualifiedClassName("java.util.Optional<java.lang.String>");
    assertEquals(Type.optional(Type.string()).withTypeParameters(PList.empty()), type);
  }
}
