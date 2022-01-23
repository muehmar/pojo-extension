package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method which is used in the safe builder to populate a certain field. If a class is
 * annotated, all methods inside this class are used for the safe builder. Annotated methods and
 * classes (and the methods inside) must be static.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface FieldBuilder {
  /** Name of the field. */
  String fieldName();
}
