package io.github.muehmar.pojoextension.generator.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Name;
import org.junit.jupiter.api.Test;

class TypeVariableTypeTest {

  @Test
  void getTypeDeclaration_when_typeVariable_then_returnsTypeVariableNameOnly() {
    final TypeVariableType typeVariableType =
        TypeVariableType.ofNameAndUpperBounds(
            Name.fromString("T"),
            PList.single(Types.comparable(Types.typeVariable(Name.fromString("T")))));

    assertEquals("T", typeVariableType.getTypeDeclaration().asString());
  }

  @Test
  void getTypeVariableDeclaration_when_typeVariable_then_returnsDeclarationWithUpperBounds() {
    final TypeVariableType typeVariableType =
        TypeVariableType.ofNameAndUpperBounds(
            Name.fromString("T"),
            PList.of(Types.comparable(Types.typeVariable(Name.fromString("T"))), Types.string()));

    assertEquals(
        "T extends Comparable<T> & String",
        typeVariableType.getTypeVariableDeclaration().asString());
  }
}
