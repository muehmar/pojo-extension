package io.github.muehmar.pojoextension.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PackageNameTest {
  @Test
  void qualifiedName_when_called_then_correctQualifiedName() {
    final Name customer = Name.fromString("Customer");
    final PackageName packageName = PackageName.fromString("io.github.muehmar");

    final Name qualifiedName = packageName.qualifiedName(customer);
    assertEquals("io.github.muehmar.Customer", qualifiedName.asString());
  }
}
