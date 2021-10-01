package io.github.muehmar.pojoextension.generator.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaModifiersTest {

  @ParameterizedTest
  @MethodSource("privateStaticFinalModifiersMultipleTimesAndDifferentOrder")
  void
      asString_when_privateStaticFinalModifiersMultipleTimesAndDifferentOrder_then_singleOccurenceAndCorrectOrder(
          PList<JavaModifier> modifiers) {
    assertEquals("private static final", JavaModifiers.of(modifiers).asString());
  }

  private static Stream<Arguments> privateStaticFinalModifiersMultipleTimesAndDifferentOrder() {
    return Stream.of(
        Arguments.of(PList.of(JavaModifier.FINAL, JavaModifier.PRIVATE, JavaModifier.STATIC)),
        Arguments.of(PList.of(JavaModifier.FINAL, JavaModifier.STATIC, JavaModifier.PRIVATE)),
        Arguments.of(PList.of(JavaModifier.STATIC, JavaModifier.FINAL, JavaModifier.PRIVATE)),
        Arguments.of(PList.of(JavaModifier.PRIVATE, JavaModifier.FINAL, JavaModifier.STATIC)),
        Arguments.of(PList.of(JavaModifier.PRIVATE, JavaModifier.STATIC, JavaModifier.FINAL)),
        Arguments.of(PList.of(JavaModifier.STATIC, JavaModifier.PRIVATE, JavaModifier.FINAL)),
        Arguments.of(
            PList.of(
                JavaModifier.STATIC,
                JavaModifier.FINAL,
                JavaModifier.PRIVATE,
                JavaModifier.FINAL,
                JavaModifier.STATIC)));
  }

  @Test
  void asStringTrailingWhiteSpace_when_noModifiers_then_outputEmpty() {
    final JavaModifiers modifiers = JavaModifiers.of();
    assertEquals("", modifiers.asStringTrailingWhitespace());
  }

  @Test
  void asStringTrailingWhiteSpace_when_atLeastOneModifier_then_outputWithTrailingWhitespace() {
    final JavaModifiers modifiers = JavaModifiers.of(JavaModifier.PRIVATE);
    assertEquals("private ", modifiers.asStringTrailingWhitespace());
  }
}
