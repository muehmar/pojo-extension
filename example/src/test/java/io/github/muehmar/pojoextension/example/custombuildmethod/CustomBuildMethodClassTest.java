package io.github.muehmar.pojoextension.example.custombuildmethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CustomBuildMethodClassTest {

  @Test
  void build_when_builderUsed_then_customBuildMethodApplied() {
    final String output =
        CustomBuildMethodClassBuilder.<String>create()
            .prop1("prop1")
            .prop2("prop2")
            .andAllOptionals()
            .data("data")
            .build();

    assertEquals("CustomBuildMethodClass(prop1=prop1, prop2=prop2, data=Optional[data])", output);
  }
}
