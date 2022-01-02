package io.github.muehmar.pojoextension.example.customannotations;

@DisabledEqualsHashCode
public class DisabledEqualsHashCodeClass implements DisabledEqualsHashCodeClassExtension {
  private final String prop1;

  public DisabledEqualsHashCodeClass(String prop1) {
    this.prop1 = prop1;
  }

  public String getProp1() {
    return prop1;
  }

  @Override
  public String toString() {
    return genToString();
  }
}
