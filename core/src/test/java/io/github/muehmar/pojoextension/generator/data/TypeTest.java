package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

  @ParameterizedTest
  @ValueSource(strings = {"int", "byte", "short", "long", "float", "double", "boolean", "char"})
  void fromQualifiedClassName_when_genericClass_then_parsedCorrectlyWithoutTypeParameter(
      String primitiveType) {
    final Type type = Type.fromQualifiedClassName(primitiveType);
    assertEquals(0, type.getTypeParameters().size());
    assertEquals(PackageName.javaLang(), type.getPackage());
    assertEquals(primitiveType, type.getClassName().asString());
  }

  @Test
  void getClassName_when_javaMap_then_correctNameReturned() {
    final Type type = Type.map(Type.string(), Type.integer());
    assertEquals("Map<String,Integer>", type.getClassName().asString());
  }

  @Test
  void equalsIgnoreTypeParameter_when_twoOptionalsWithDifferentTypeParameter_then_areEquals() {
    final Type optionalString = Type.optional(Type.string());
    final Type optionalInteger = Type.optional(Type.integer());
    assertNotEquals(optionalInteger, optionalString);
    assertTrue(optionalString.equalsIgnoreTypeParameters(optionalInteger));
  }
}
