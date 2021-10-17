package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;

public class Constructor {
  private final Name name;
  private final PList<Argument> arguments;

  public Constructor(Name name, PList<Argument> arguments) {
    this.name = name;
    this.arguments = arguments;
  }

  public Name getName() {
    return name;
  }

  public PList<Argument> getArguments() {
    return arguments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Constructor that = (Constructor) o;
    return Objects.equals(name, that.name) && Objects.equals(arguments, that.arguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, arguments);
  }

  @Override
  public String toString() {
    return "Constructor{" + "name=" + name + ", arguments=" + arguments + '}';
  }
}
