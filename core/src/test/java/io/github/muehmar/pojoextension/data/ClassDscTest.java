package io.github.muehmar.pojoextension.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ClassDscTest {
  @ParameterizedTest
  @CsvSource({"io.github.muehmar.pojoextension.data,ClassName"})
  void fromFullClassName_when_(String pkg, String clazz) {
    final ClassDsc classDsc = ClassDsc.fromFullClassName(String.format("%s.%s", pkg, clazz));

    assertEquals(pkg, classDsc.getPkg().asString());
    assertEquals(clazz, classDsc.getName().asString());
  }
}
