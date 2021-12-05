package io.github.muehmar.pojoextension.example.safebuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BuilderClassTest {

  @Test
  void builderWithCustomNameCreated() {
    final BuilderClass builderClass =
        SafeBuilder.create().setProp1("prop1").setProp2("prop2").build();

    assertEquals("prop1", builderClass.getProp1());
    assertEquals("prop2", builderClass.getProp2());
  }
}
