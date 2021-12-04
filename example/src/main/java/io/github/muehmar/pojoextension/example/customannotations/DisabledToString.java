package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
public @interface DisabledToString {
  /** Enables or disables the generation of the toString method. */
  boolean enableToString() default false;
}
