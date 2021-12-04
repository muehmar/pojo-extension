package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
public @interface DisabledMappers {
  /** Enables or disables the generation of the map methods. */
  boolean enableMappers() default false;
}
