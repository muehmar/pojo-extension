package io.github.muehmar.pojoextension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringsTest {

  @Test
  void surroundIfNotEmpty_when_calledWithEmptyContent_then_returnsEmpty() {
    final String result = Strings.surroundIfNotEmpty("prefix", "", "suffix");
    assertEquals("", result);
  }

  @Test
  void surroundIfNotEmpty_when_calledNonEmptyContent_then_contentPrefixedAndSuffixed() {
    final String result = Strings.surroundIfNotEmpty("prefix", "_content_", "suffix");
    assertEquals("prefix_content_suffix", result);
  }
}
