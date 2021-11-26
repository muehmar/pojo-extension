package io.github.muehmar.pojoextension.annotations;

/**
 * Mark a method as getter in case the name does not conform to the java-beans naming convention.
 */
public @interface Getter {
  /** Name of the field. */
  String value();
}
