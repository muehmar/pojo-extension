package io.github.muehmar.pojoextension.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class GenericTest {

  @Test
  void getTypeDeclaration_when_called_then_correctFormatted() {
    final Generic generic =
        GenericBuilder.create()
            .setTypeVariable(Name.fromString("T"))
            .setUpperBounds(
                PList.of(
                    Type.list(Type.string()),
                    Type.comparable(Type.typeVariable(Name.fromString("T")))))
            .build();

    assertEquals("T extends List<String> & Comparable<T>", generic.getTypeDeclaration().asString());
  }
}
