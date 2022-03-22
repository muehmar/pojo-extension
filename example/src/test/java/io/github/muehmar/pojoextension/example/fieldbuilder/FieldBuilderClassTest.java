package io.github.muehmar.pojoextension.example.fieldbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FieldBuilderClassTest {
  @Test
  void randomString_when_twoInstancesCreated_then_notEqualsProp1() {
    final FieldBuilderClass<String> fieldBuilder1 =
        FieldBuilderClassBuilder.<String>create().randomString().prop2("asd").build();
    final FieldBuilderClass<String> fieldBuilder2 =
        FieldBuilderClassBuilder.<String>create().randomString().prop2("asd").build();

    assertNotEquals(fieldBuilder1.getProp1(), fieldBuilder2.getProp1());
  }

  @Test
  void builderForProp1ContainsOnlyCustomMethods() {
    final Class<?> clazz = FieldBuilderClassBuilder.Builder0.class;
    assertFalse(classHasMethod(clazz, "prop1"));
    assertTrue(classHasMethod(clazz, "randomString"));
    assertTrue(classHasMethod(clazz, "fromInt"));
    assertTrue(classHasMethod(clazz, "fromVarargs"));
  }

  @Test
  void fromInt_when_usedInBuilder_then_prop1IsIntegerString() {
    final FieldBuilderClass<String> fieldBuilder =
        FieldBuilderClassBuilder.<String>create().fromInt(52).prop2("asd").build();

    assertEquals("52", fieldBuilder.getProp1());
  }

  @Test
  void fromVarargs_when_usedInBuilder_then_compilesAndCorrectProp1() {
    final FieldBuilderClass<String> fieldBuilder =
        FieldBuilderClassBuilder.<String>create()
            .fromVarargs("hello", "world", "!")
            .prop2("prop2")
            .build();

    assertEquals("hello-world-!", fieldBuilder.getProp1());
  }

  @Test
  void constant_when_usedInBuilder_then_prop3WithConstantValue() {
    final FieldBuilderClass<String> fieldBuilder =
        FieldBuilderClassBuilder.<String>create()
            .randomString()
            .prop2("asd")
            .andAllOptionals()
            .constant()
            .data("")
            .build();

    assertEquals(Optional.of("CONSTANT"), fieldBuilder.getProp3());
  }

  @Test
  void supplier_when_usedInBuilder_then_dataFilledFromSupplier() {
    final FieldBuilderClass<String> fieldBuilder =
        FieldBuilderClassBuilder.<String>create()
            .randomString()
            .prop2("asd")
            .andAllOptionals()
            .constant()
            .supplier(() -> "supplierData")
            .build();

    assertEquals(Optional.of("supplierData"), fieldBuilder.getData());
  }

  private static boolean classHasMethod(Class<?> clazz, String methodName) {
    return Arrays.stream(clazz.getDeclaredMethods())
        .anyMatch(method -> method.getName().equals(methodName));
  }
}
