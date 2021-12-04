package io.github.muehmar.pojoextension.example.customannotations;

import static io.github.muehmar.pojoextension.example.MethodHelper.EQUALS;
import static io.github.muehmar.pojoextension.example.MethodHelper.HASH_CODE;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF_PRESENT;
import static io.github.muehmar.pojoextension.example.MethodHelper.TO_STRING;
import static io.github.muehmar.pojoextension.example.MethodHelper.hasMethod;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisabledEqualsHashCodeClassTest {
  @Test
  void extensionHasNoEqualsAndHashCodeMethods() {
    final Class<?> clazz = DisabledEqualsHashCodeClassExtension.class;

    assertTrue(hasMethod(clazz, "withProp1"));

    assertFalse(hasMethod(clazz, EQUALS));
    assertFalse(hasMethod(clazz, HASH_CODE));

    assertTrue(hasMethod(clazz, TO_STRING));

    assertTrue(hasMethod(clazz, MAP));
    assertTrue(hasMethod(clazz, MAP_IF));
    assertTrue(hasMethod(clazz, MAP_IF_PRESENT));
  }
}
