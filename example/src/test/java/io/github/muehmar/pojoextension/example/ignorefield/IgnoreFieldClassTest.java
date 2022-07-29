package io.github.muehmar.pojoextension.example.ignorefield;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IgnoreFieldClassTest {

  @Test
  void safeBuilderUsed_when_constructed_then_correctInstanceCreated() {
    final IgnoreFieldClass ignoreFieldClass =
        IgnoreFieldClassBuilder.create().id("1234").name("Martin").andAllOptionals().build();

    assertEquals("1234", ignoreFieldClass.getId());
    assertEquals("Martin", ignoreFieldClass.getName());
    assertEquals("1234-Martin", ignoreFieldClass.getDeviated());
  }
}
