package io.github.muehmar.pojoextension.generator.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class LineTest {
  @Test
  void append_when_calledWithAdditionalFragment_then_fragmentAppended() {
    final Line helloWorld = Line.ofString("Hello").append(" World!");
    assertEquals("Hello World!", helloWorld.asString());
  }

  @Test
  void prepend_when_calledWithAdditionalFragment_then_fragmentPrepended() {
    final Line helloWorld = Line.ofString("World!").prepend("Hello ");
    assertEquals("Hello World!", helloWorld.asString());
  }

  @Test
  void prependList_when_calledWithAdditionalFragments_then_fragmentsPrepended() {
    final Line helloWorld = Line.ofString("World!").prepend(PList.of("Hello", " "));
    assertEquals("Hello World!", helloWorld.asString());
  }

  @Test
  void
      removeTrailingBlankFragments_when_lineWithDifferentBlankFragments_then_trailingBlankFragmentsRemoved() {
    final Line line =
        Line.ofString(" ").append("Hello").append(" ").append("World!").append("  ").append(" ");

    assertEquals(" Hello World!", line.removeTrailingBlankFragments().asString());
  }
}
