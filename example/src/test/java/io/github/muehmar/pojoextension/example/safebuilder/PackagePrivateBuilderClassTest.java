package io.github.muehmar.pojoextension.example.safebuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class PackagePrivateBuilderClassTest {

  @Test
  void getClassModifiers_when_calledForBuilderClass_then_isFinalAndPackagePrivate() {
    final int modifiers = PackagePrivateBuilderClassBuilder.class.getModifiers();

    assertFalse(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isFinal(modifiers));
  }
}
