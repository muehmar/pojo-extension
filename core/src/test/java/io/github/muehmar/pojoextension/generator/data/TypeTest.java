package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TypeTest {

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
  void onOptional_when_typeIsOptional_then_functionAppliedWithTypeParameter() {
    final Type type = Type.optional(Type.string());
    assertEquals(Optional.of("String"), type.onOptional(Type::getClassName).map(Name::asString));
  }

  @Test
  void onOptional_when_typeIsNotOptional_then_emptyReturned() {
    final Type type = Type.map(Type.string(), Type.integer());
    assertEquals(Optional.empty(), type.onOptional(Type::getClassName).map(Name::asString));
  }

  @Test
  void isPrimitive_when_calledForEachPrimitive_then_trueForAll() {
    final PList<Type> primitives = Type.allPrimitives();
    primitives.forEach(primitive -> assertTrue(primitive.isPrimitive()));
  }

  @Test
  void isPrimitive_when_calledForNonPrimitives_then_falseForAll() {
    assertFalse(Type.string().isPrimitive());
    assertFalse(Type.integer().isPrimitive());
  }

  @Test
  void isVoid_when_calledForVoidType_then_true() {
    assertTrue(Type.voidType().isVoid());
  }

  @Test
  void getRelation_when_unrelatedTypes_then_noRelationReturned() {
    final Optional<OptionalFieldRelation> relation = Type.string().getRelation(Type.integer());
    assertFalse(relation.isPresent());
  }

  @Test
  void getRelation_when_stringAndOptionalString_then_relationIsWrapIntoOptional() {
    final Optional<OptionalFieldRelation> relation =
        Type.string().getRelation(Type.optional(Type.string()));
    assertEquals(Optional.of(OptionalFieldRelation.WRAP_INTO_OPTIONAL), relation);
  }

  @Test
  void getRelation_when_optionalStringAndString_then_relationIsUnwrapOptional() {
    final Optional<OptionalFieldRelation> relation =
        Type.optional(Type.string()).getRelation(Type.string());
    assertEquals(Optional.of(OptionalFieldRelation.UNWRAP_OPTIONAL), relation);
  }

  @Test
  void getRelation_when_bothAreStrings_then_relationIsSameType() {
    final Optional<OptionalFieldRelation> relation = Type.string().getRelation(Type.string());
    assertEquals(Optional.of(OptionalFieldRelation.SAME_TYPE), relation);
  }

  private static Stream<Arguments> primitiveTypes() {
    return Type.allPrimitives()
        .toStream()
        .map(Type::getName)
        .map(Name::asString)
        .map(Arguments::of);
  }
}
