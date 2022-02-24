package io.github.muehmar.pojoextension.generator.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PackageName;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClassnameParserTest {

  @Test
  void parse_when_javaLangString_then_parsedCorrectly() {
    final Optional<ClassnameParser.NameAndPackage> parse =
        ClassnameParser.parse("java.lang.String");
    assertTrue(parse.isPresent());
    assertEquals(Name.fromString("String"), parse.get().getName());
    assertEquals(Optional.of(PackageName.javaLang()), parse.get().getPkg());
  }

  @Test
  void parse_when_genericClass_then_parsedCorrectlyWithoutTypeParameter() {
    final Optional<ClassnameParser.NameAndPackage> parse =
        ClassnameParser.parse("java.util.Optional<java.lang.String>");
    assertTrue(parse.isPresent());
    assertEquals(Name.fromString("Optional"), parse.get().getName());
    assertEquals(Optional.of(PackageName.javaUtil()), parse.get().getPkg());
  }

  @Test
  void parse_when_innerClassName_then_parsedCorrectly() {
    final Optional<ClassnameParser.NameAndPackage> parse =
        ClassnameParser.parse("io.github.muehmar.Customer.Address");
    assertTrue(parse.isPresent());
    assertEquals(Name.fromString("Customer.Address"), parse.get().getName());
    assertEquals(Optional.of(PackageName.fromString("io.github.muehmar")), parse.get().getPkg());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1Invalid", "org.123.Customer"})
  void parse_when_invalidClassName_then_returnEmpty(String input) {
    final Optional<ClassnameParser.NameAndPackage> result = ClassnameParser.parse(input);
    assertFalse(result.isPresent(), "Parsed to " + result);
  }
}
