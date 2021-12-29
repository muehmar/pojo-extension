package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@PojoExtension(
    enableWithers = false,
    enableMappers = false,
    enableToString = false,
    enableEqualsAndHashCode = false)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface SafeBuilder {
  /**
   * Override the default name which is used for the discrete builder class. `{CLASSNAME}` gets by
   * the classname of the annotated class.
   */
  String builderName() default "{CLASSNAME}Builder";
}
