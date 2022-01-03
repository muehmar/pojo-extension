package io.github.muehmar.pojoextension.example.customannotations;

import static io.github.muehmar.pojoextension.example.MethodHelper.EQUALS;
import static io.github.muehmar.pojoextension.example.MethodHelper.GEN_EQUALS;
import static io.github.muehmar.pojoextension.example.MethodHelper.GEN_HASH_CODE;
import static io.github.muehmar.pojoextension.example.MethodHelper.GEN_TO_STRING;
import static io.github.muehmar.pojoextension.example.MethodHelper.HASH_CODE;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF_PRESENT;
import static io.github.muehmar.pojoextension.example.MethodHelper.TO_STRING;
import static io.github.muehmar.pojoextension.example.MethodHelper.hasMethod;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisabledToStringClassTest {
  @Test
  void extensionHasNoToStringMethod() {
    final Class<?> clazz = DisabledToStringClassExtension.class;

    assertTrue(hasMethod(clazz, "withProp1"));

    assertTrue(hasMethod(clazz, GEN_EQUALS));
    assertTrue(hasMethod(clazz, GEN_HASH_CODE));

    assertFalse(hasMethod(clazz, GEN_TO_STRING));

    assertTrue(hasMethod(clazz, MAP));
    assertTrue(hasMethod(clazz, MAP_IF));
    assertTrue(hasMethod(clazz, MAP_IF_PRESENT));
  }

  @Test
  void baseClassHasNoToStringMethod() {
    final Class<?> clazz = DisabledToStringClassBase.class;

    assertTrue(hasMethod(clazz, EQUALS));
    assertTrue(hasMethod(clazz, HASH_CODE));

    assertFalse(hasMethod(clazz, TO_STRING));
  }
}
