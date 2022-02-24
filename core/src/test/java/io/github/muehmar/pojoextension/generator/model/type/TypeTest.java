package io.github.muehmar.pojoextension.generator.model.type;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_INTEGER;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class TypeTest {

  @Test
  void getImports_when_javaMap_then_correctQualifiedNames() {
    final Type type = Types.map(Types.string(), Types.integer());
    final PList<String> allQualifiedNames = type.getImports().map(Name::asString);
    assertEquals(3, allQualifiedNames.size());
    assertTrue(allQualifiedNames.exists(JAVA_UTIL_MAP::equals));
    assertTrue(allQualifiedNames.exists(JAVA_LANG_STRING::equals));
    assertTrue(allQualifiedNames.exists(JAVA_LANG_INTEGER::equals));
  }

  @ParameterizedTest
  @EnumSource(PrimitiveType.class)
  void isPrimitive_when_calledForEachPrimitive_then_trueForAll(PrimitiveType primitiveType) {
    final Type type = Type.fromSpecificType(primitiveType);
    assertTrue(type.isPrimitive());
  }

  @Test
  void isPrimitive_when_calledForNonPrimitives_then_falseForAll() {
    assertFalse(Types.string().isPrimitive());
    assertFalse(Types.integer().isPrimitive());
  }

  @Test
  void isVoid_when_calledForVoidType_then_true() {
    assertTrue(Types.voidType().isVoid());
  }

  @ParameterizedTest
  @MethodSource("nonArrayTypes")
  void isArray_when_calledForArray_then_true(Type type) {
    assertTrue(Types.array(type).isArray());
  }

  @ParameterizedTest
  @MethodSource("nonArrayTypes")
  void isArray_when_calledNonArrayTypes_then_false(Type type) {
    assertFalse(type.isArray());
  }

  static Stream<Arguments> nonArrayTypes() {
    return Stream.of(
        Arguments.of(Types.booleanClass()),
        Arguments.of(Types.voidType()),
        Arguments.of(Types.integer()),
        Arguments.of(Types.primitiveChar()));
  }

  @Test
  void getRelation_when_unrelatedTypes_then_noRelationReturned() {
    final Optional<OptionalFieldRelation> relation = Types.string().getRelation(Types.integer());
    assertFalse(relation.isPresent());
  }

  @Test
  void getRelation_when_stringAndOptionalString_then_relationIsWrapIntoOptional() {
    final Optional<OptionalFieldRelation> relation =
        Types.string().getRelation(Types.optional(Types.string()));
    assertEquals(Optional.of(OptionalFieldRelation.WRAP_INTO_OPTIONAL), relation);
  }

  @Test
  void getRelation_when_optionalStringAndString_then_relationIsUnwrapOptional() {
    final Optional<OptionalFieldRelation> relation =
        Types.optional(Types.string()).getRelation(Types.string());
    assertEquals(Optional.of(OptionalFieldRelation.UNWRAP_OPTIONAL), relation);
  }

  @Test
  void getRelation_when_bothAreStrings_then_relationIsSameType() {
    final Optional<OptionalFieldRelation> relation = Types.string().getRelation(Types.string());
    assertEquals(Optional.of(OptionalFieldRelation.SAME_TYPE), relation);
  }

  @Test
  void fold_when_declaredType_then_correspondingFunctionExecutedWithDeclaredType() {
    final Type type = Types.optional(Types.string());

    final Function<DeclaredType, String> getTypeDeclaration =
        declaredType -> declaredType.getTypeDeclaration().asString();

    assertEquals(
        "Optional<String>",
        type.fold(getTypeDeclaration, ignore -> "", ignore -> "", ignore -> ""));
  }

  @Test
  void fold_when_arrayType_then_correspondingFunctionExecutedWithArrayType() {
    final Type type = Types.array(Types.string());

    final Function<ArrayType, String> getItemTypeName =
        arrayType -> arrayType.getItemType().getName().asString();
    assertEquals("String", type.fold(ignore -> "", getItemTypeName, ignore -> "", ignore -> ""));
  }

  @Test
  void fold_when_primitiveType_then_correspondingFunctionExecutedWithPrimitiveType() {
    final Type type = Types.primitiveBoolean();

    final Function<PrimitiveType, String> getPrimitiveName =
        primitiveType -> primitiveType.getName().asString();
    assertEquals("boolean", type.fold(ignore -> "", ignore -> "", getPrimitiveName, ignore -> ""));
  }

  @Test
  void fold_when_typeVariable_then_correspondingFunctionExecutedWithTypeVariableType() {
    final Type type = Types.typeVariable(Name.fromString("T"));

    final Function<TypeVariableType, Boolean> compareTypeVariableName =
        typeVariableType -> typeVariableType.getName().equals(Name.fromString("T"));

    assertTrue(
        type.fold(ignore -> false, ignore -> false, ignore -> false, compareTypeVariableName));
  }

  @Test
  void onDeclaredType_when_declaredType_then_functionExecuted() {
    final Type type = Types.optional(Types.string());

    final Function<DeclaredType, String> getTypeDeclaration =
        declaredType -> declaredType.getTypeDeclaration().asString();

    final Optional<String> result = type.onDeclaredType(getTypeDeclaration);
    assertEquals(Optional.of("Optional<String>"), result);
  }

  @Test
  void onDeclaredType_when_nonDeclaredType_then_returnsEmptyOptional() {
    final Function<DeclaredType, String> getTypeDeclaration =
        declaredType -> declaredType.getTypeDeclaration().asString();

    assertFalse(Types.array(Types.string()).onDeclaredType(getTypeDeclaration).isPresent());
    assertFalse(
        Types.typeVariable(Name.fromString("T")).onDeclaredType(getTypeDeclaration).isPresent());
    assertFalse(Types.primitiveBoolean().onDeclaredType(getTypeDeclaration).isPresent());
  }

  @Test
  void onArrayType_when_arrayType_then_functionExecuted() {
    final Type type = Types.array(Types.string());

    final Function<ArrayType, String> getItemType =
        arrayType -> arrayType.getItemType().getTypeDeclaration().asString();

    final Optional<String> result = type.onArrayType(getItemType);
    assertEquals(Optional.of("String"), result);
  }

  @Test
  void onArrayType_when_nonArrayType_then_returnsEmptyOptional() {
    final Function<ArrayType, String> getItemType =
        arrayType -> arrayType.getItemType().getTypeDeclaration().asString();

    assertFalse(Types.string().onArrayType(getItemType).isPresent());
    assertFalse(Types.typeVariable(Name.fromString("T")).onArrayType(getItemType).isPresent());
    assertFalse(Types.primitiveBoolean().onArrayType(getItemType).isPresent());
  }
}
