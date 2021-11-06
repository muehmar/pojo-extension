package io.github.muehmar.pojoextension.generator.data;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;

@PojoExtension
public class Getter extends GetterExtension {
  private final Name name;
  private final Type returnType;

  public Getter(Name name, Type returnType) {
    this.name = name;
    this.returnType = returnType;
  }

  public Name getName() {
    return name;
  }

  public Type getReturnType() {
    return returnType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Getter getter = (Getter) o;
    return Objects.equals(name, getter.name) && Objects.equals(returnType, getter.returnType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, returnType);
  }

  @Override
  public String toString() {
    return "Getter{" + "name=" + name + ", returnType=" + returnType + '}';
  }
}
