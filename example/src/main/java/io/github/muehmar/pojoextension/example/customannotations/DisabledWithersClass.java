package io.github.muehmar.pojoextension.example.customannotations;

@DisabledWithers
public class DisabledWithersClass extends DisabledWithersClassBase {
  private final String prop1;

  public DisabledWithersClass(String prop1) {
    this.prop1 = prop1;
  }

  public String getProp1() {
    return prop1;
  }
}
