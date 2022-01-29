package io.github.muehmar.pojoextension.example.fieldbuilder;

import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@SafeBuilder
public class FieldBuilderClass<T> {
  private final String prop1;
  private final String prop2;
  private final Optional<String> prop3;
  private final Optional<T> data;

  FieldBuilderClass(String prop1, String prop2, Optional<String> prop3, Optional<T> data) {
    this.prop1 = prop1;
    this.prop2 = prop2;
    this.prop3 = prop3;
    this.data = data;
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

  public Optional<T> getData() {
    return data;
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

  @FieldBuilder(fieldName = "data")
  static <T> T supplier(Supplier<T> s) {
    return s.get();
  }
}
