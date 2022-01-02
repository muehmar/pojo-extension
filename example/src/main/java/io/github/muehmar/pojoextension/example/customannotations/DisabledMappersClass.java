package io.github.muehmar.pojoextension.example.customannotations;

@DisabledMappers
public class DisabledMappersClass implements DisabledMappersClassExtension {
  private final String prop1;

  public DisabledMappersClass(String prop1) {
    this.prop1 = prop1;
  }

  public String getProp1() {
    return prop1;
  }

  @Override
  public boolean equals(Object o) {
    return genEquals(o);
  }

  @Override
  public int hashCode() {
    return genHashCode();
  }

  @Override
  public String toString() {
    return genToString();
  }
}
