package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
public @interface DisabledWithers {
  /** Enables or disables the generation of the with methods. */
  boolean enableWithers() default false;
}
