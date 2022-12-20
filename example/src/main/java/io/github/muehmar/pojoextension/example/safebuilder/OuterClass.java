package io.github.muehmar.pojoextension.example.safebuilder;

import lombok.Value;

public class OuterClass {
  private OuterClass() {}

  @Value
  public static class InnerClass {
    String value;
  }
}
