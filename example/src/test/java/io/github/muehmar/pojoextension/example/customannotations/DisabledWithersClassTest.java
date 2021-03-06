package io.github.muehmar.pojoextension.example.customannotations;

import static io.github.muehmar.pojoextension.example.MethodHelper.MAP;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF;
import static io.github.muehmar.pojoextension.example.MethodHelper.MAP_IF_PRESENT;
import static io.github.muehmar.pojoextension.example.MethodHelper.hasMethod;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisabledWithersClassTest {
  @Test
  void extensionHasNoWithMethod() {
    final Class<?> clazz = DisabledWithersClassExtension.class;

    assertFalse(hasMethod(clazz, "withProp1"));

    assertTrue(hasMethod(clazz, MAP));
    assertTrue(hasMethod(clazz, MAP_IF));
    assertTrue(hasMethod(clazz, MAP_IF_PRESENT));
  }
}
