package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@PojoExtension
public @interface DisabledWithers {
  /** Enables or disables the generation of the with methods. */
  boolean enableWithers() default false;
}
