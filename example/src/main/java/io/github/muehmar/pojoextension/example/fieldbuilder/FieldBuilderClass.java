package io.github.muehmar.pojoextension.example.fieldbuilder;

import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.Optional;
import java.util.UUID;

@SafeBuilder
public class FieldBuilderClass {
  private final String prop1;
  private final String prop2;
  private final Optional<String> prop3;

  public FieldBuilderClass(String prop1, String prop2, Optional<String> prop3) {
    this.prop1 = prop1;
    this.prop2 = prop2;
    this.prop3 = prop3;
  }

  public String getProp1() {
    return prop1;
  }

  public String getProp2() {
    return prop2;
  }

  public Optional<String> getProp3() {
    return prop3;
  }

  @FieldBuilder(fieldName = "prop1")
  static class Prop1Builder {
    private Prop1Builder() {}

    static String randomString() {
      return UUID.randomUUID().toString();
    }

    static String fromInt(Integer i) {
      return i.toString();
    }
  }

  @FieldBuilder(fieldName = "prop3")
  static class Prop3Builder {
    private Prop3Builder() {}

    static String constant() {
      return "CONSTANT";
    }
  }
}
