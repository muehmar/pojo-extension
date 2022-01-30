package io.github.muehmar.pojoextension.example.customannotations;

@DisabledToString
public class DisabledToStringClass extends DisabledToStringClassBase {
  private final String prop1;

  public DisabledToStringClass(String prop1) {
    this.prop1 = prop1;
  }

  public String getProp1() {
    return prop1;
  }

  static String emptyProp() {
    return "";
  }
}
