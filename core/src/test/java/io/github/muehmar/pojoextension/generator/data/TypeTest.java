package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TypeTest {
  private static final PList<String> primitiveTypes =
      PList.of("int", "byte", "short", "long", "float", "double", "boolean", "char");

  @Test
  void fromClassName_when_javaLangString_then_parsedCorrectly() {
    final Type type = Type.fromClassName("java.lang.String");
    assertEquals(Type.string(), type);
  }

  @Test
  void fromClassName_when_genericClass_then_parsedCorrectlyWithoutTypeParameter() {
    final Type type = Type.fromClassName("java.util.Optional<java.lang.String>");
    assertEquals(Type.optional(Type.string()).withTypeParameters(PList.empty()), type);
  }

  @ParameterizedTest
  @MethodSource("primitiveTypes")
  void fromClassName_when_primitiveType_then_parsedCorrectly(String primitiveType) {
    final Type type = Type.fromClassName(primitiveType);
    assertEquals(Type.primitive(primitiveType), type);
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

  private static Stream<Arguments> primitiveTypes() {
    return primitiveTypes.toStream().map(Arguments::of);
  }
}
