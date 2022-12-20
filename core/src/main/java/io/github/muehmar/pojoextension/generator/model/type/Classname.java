package io.github.muehmar.pojoextension.generator.model.type;

import io.github.muehmar.pojoextension.generator.model.Name;
import lombok.EqualsAndHashCode;

/**
 * Name of a class. Could also be an inner class, where the name would be including the outer
 * classname, like OuterClass.InnerClass.
 */
@EqualsAndHashCode
public class Classname {
  private final Name fullName;

  private Classname(Name fullName) {
    this.fullName = fullName;
  }

  public static Classname fromFullClassName(String fullClassName) {
    return new Classname(Name.fromString(fullClassName));
  }

  /**
   * Returns the outer class in case of an inner class or the classname of the class itself in case
   * the class is a top level class.
   */
  public Name getOuterClassname() {
    final String name = fullName.asString();
    final int i = name.indexOf(".");
    if (i >= 0) {
      return Name.fromString(name.substring(0, i));
    }
    return fullName;
  }

  public Name asName() {
    return fullName;
  }

  public String asString() {
    return fullName.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
