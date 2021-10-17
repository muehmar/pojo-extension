package io.github.muehmar.pojoextension.generator.data;

import java.util.Objects;

public class Argument {
  private final Name name;
  private final Type type;

  public Argument(Name name, Type type) {
    this.name = name;
    this.type = type;
  }

  public Name getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Argument argument = (Argument) o;
    return Objects.equals(name, argument.name) && Objects.equals(type, argument.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }

  @Override
  public String toString() {
    return "Argument{" + "name=" + name + ", type=" + type + '}';
  }
}
