package io.github.muehmar.pojoextension.generator.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.impl.gen.Refs;
import io.github.muehmar.pojoextension.generator.model.Name;
import org.junit.jupiter.api.Test;

class ArrayTypeTest {

  @Test
  void getTypeDeclaration_when_stringArray_then_correctDeclaration() {
    final ArrayType arrayType = ArrayType.fromItemType(Types.string());
    assertEquals("String[]", arrayType.getTypeDeclaration().asString());
  }

  @Test
  void getName_when_stringArray_then_correctName() {
    final ArrayType arrayType = ArrayType.fromItemType(Types.string());
    assertEquals("String[]", arrayType.getName().asString());
  }

  @Test
  void getImports_when_stringArray_then_allImports() {
    final ArrayType arrayType = ArrayType.fromItemType(Types.string());
    assertEquals(PList.single(Refs.JAVA_LANG_STRING), arrayType.getImports().map(Name::asString));
  }
}
