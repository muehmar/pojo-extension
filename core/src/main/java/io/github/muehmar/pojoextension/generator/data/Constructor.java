package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;
import java.util.Optional;

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

  /**
   * Return the list of fields in the order of the match of the arguments for this constructor or
   * empty if they do not match.
   */
  public Optional<PList<PojoField>> matchFields(PList<PojoField> fields) {
    if (fields.size() != arguments.size()) {
      return Optional.empty();
    }

    return fields.zip(arguments).forall(p -> p.second().matchesType(p.first()))
        ? Optional.of(fields)
        : Optional.empty();
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
