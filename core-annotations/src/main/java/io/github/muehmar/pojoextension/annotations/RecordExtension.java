package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@PojoExtension
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RecordExtension {
  /** Defines how optional fields in a pojo are detected. */
  OptionalDetection[] optionalDetection() default {OptionalDetection.OPTIONAL_CLASS};

  /**
   * Override the default name which is used for the extension interface. `{CLASSNAME}` gets
   * replaced by the classname of the annotated class.
   */
  String extensionName() default "{CLASSNAME}Extension";

  /** Enables or disables the generation of the SafeBuilder. */
  boolean enableSafeBuilder() default true;

  /**
   * Override the default name which is used for the discrete builder class. `{CLASSNAME}` gets
   * replaced by the classname of the annotated class.
   */
  String builderName() default "{CLASSNAME}Builder";

  /** Prefix which is used for the setter methods of the builder. */
  String builderSetMethodPrefix() default "";

  /** Generates a package-private builder which is only accessible from within the same package. */
  boolean packagePrivateBuilder() default false;

  /** Enables or disables the generation of the with methods. */
  boolean enableWithers() default true;

  /**
   * Enables or disables the generation of the convenience getters for optional fields with a
   * default value if not present.
   */
  boolean enableOptionalGetters() default true;

  /** Enables or disables the generation of the map methods. */
  boolean enableMappers() default true;
}
