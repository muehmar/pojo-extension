package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@PojoExtension(enableWithers = false, enableOptionalGetters = false, enableMappers = false)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface SafeBuilder {
  /** Defines how optional fields in a pojo are detected. */
  OptionalDetection[] optionalDetection() default {
    OptionalDetection.OPTIONAL_CLASS, OptionalDetection.NULLABLE_ANNOTATION
  };

  /**
   * Override the default name which is used for the discrete builder class. `{CLASSNAME}` gets by
   * the classname of the annotated class.
   */
  String builderName() default "{CLASSNAME}Builder";

  /** Prefix which is used for the setter methods of the builder. */
  String builderSetMethodPrefix() default "";

  /** Generates a package-private builder which is only accessible from within the same package. */
  boolean packagePrivateBuilder() default false;
}
