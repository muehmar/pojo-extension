package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as getter in case the name does not conform to the java-beans naming convention.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Getter {
  /** Name of the field. */
  String value();
}
