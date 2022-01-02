package io.github.muehmar.pojoextension.example.customannotations;

public abstract class AllRequiredClassAdapter implements CustomExtension {

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
