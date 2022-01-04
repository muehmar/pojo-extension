package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Creates an extension class for the annotated class. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PojoExtension {

  /** Defines how optional fields in a pojo are detected. */
  OptionalDetection[] optionalDetection() default {
    OptionalDetection.OPTIONAL_CLASS, OptionalDetection.NULLABLE_ANNOTATION
  };

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

  /**
   * Enables or disables the generation of the equals and hashCode methods. As equals and hashCode
   * methods are not allowed as default methods in interfaces, the methods with the name {@code
   * genEquals} and {@code genHashCode} are generated.
   *
   * <p>There are two possible ways to use the methods:
   *
   * <ul>
   *   <li>Delegate the call in the concrete data class to these methods.
   *   <li>Enable the generation of the base class which overrides these methods and inherit from
   *       it.
   * </ul>
   */
  boolean enableEqualsAndHashCode() default true;

  /**
   * Enables or disables the generation of the toString method. As the toString method is not
   * allowed as default method in interfaces, the method with the name {@code genToString} is
   * generated.
   *
   * <p>There are two possible ways to use the method:
   *
   * <ul>
   *   <li>Delegate the call in the concrete data class to this method in the interface.
   *   <li>Enable the generation of the base class which overrides this method and inherit from it.
   * </ul>
   */
  boolean enableToString() default true;

  /** Enables or disables the generation of the with methods. */
  boolean enableWithers() default true;

  /** Enables or disables the generation of the map methods. */
  boolean enableMappers() default true;

  /**
   * Creates an abstract base class which overrides {@link Object#toString()}, {@link
   * Object#equals(Object)} and {@link Object#hashCode()}. These methods are not allowed as default
   * methods in interfaces and the concrete class needs to delegate the call to the prefixed default
   * methods in the interface or extend this base class where the methods are already overridden.
   */
  boolean enableBaseClass() default true;

  /**
   * Override the default name which is used for the base class. `{CLASSNAME}` gets replaced by the
   * classname of the annotated class.
   */
  String baseClassName() default "{CLASSNAME}Base";
}
