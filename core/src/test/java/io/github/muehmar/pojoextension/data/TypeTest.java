package io.github.muehmar.pojoextension.data;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  @Test
  void getClassName_when_javaMap_then_correctNameReturned() {
    final Type type = Type.map(Type.string(), Type.integer());
    assertEquals("Map<String,Integer>", type.getClassName().asString());
  }
}
