package io.github.muehmar.pojoextension.example.customannotations;

import static io.github.muehmar.pojoextension.example.MethodHelper.GEN_EQUALS;
import static io.github.muehmar.pojoextension.example.MethodHelper.GEN_HASH_CODE;
import static io.github.muehmar.pojoextension.example.MethodHelper.GEN_TO_STRING;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF_PRESENT;
import static io.github.muehmar.pojoextension.example.MethodHelper.hasMethod;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisabledMappersClassTest {
  @Test
  void extensionHasNoMappersMethod() {
    final Class<?> clazz = DisabledMappersClassExtension.class;

    assertTrue(hasMethod(clazz, "withProp1"));

    assertTrue(hasMethod(clazz, GEN_EQUALS));
    assertTrue(hasMethod(clazz, GEN_HASH_CODE));

    assertTrue(hasMethod(clazz, GEN_TO_STRING));

    assertFalse(hasMethod(clazz, MAP));
    assertFalse(hasMethod(clazz, MAP_IF));
    assertFalse(hasMethod(clazz, MAP_IF_PRESENT));
  }
}
