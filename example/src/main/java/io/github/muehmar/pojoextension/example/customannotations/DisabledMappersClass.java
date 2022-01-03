package io.github.muehmar.pojoextension.example.customannotations;

@DisabledMappers
public class DisabledMappersClass extends DisabledMappersClassBase {
  private final String prop1;

  public DisabledMappersClass(String prop1) {
    this.prop1 = prop1;
  }

  public String getProp1() {
    return prop1;
  }
}
