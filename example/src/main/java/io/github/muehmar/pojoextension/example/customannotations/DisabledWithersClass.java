package io.github.muehmar.pojoextension.example.customannotations;

@DisabledWithers
public class DisabledWithersClass implements DisabledWithersClassExtension {
  private final String prop1;

  public DisabledWithersClass(String prop1) {
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
