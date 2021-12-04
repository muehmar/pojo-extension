package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension
public @interface DisabledEqualsHashCode {
  /** Enables or disables the generation of the equals and hashCode methods. */
  boolean enableEqualsAndHashCode() default false;
}
