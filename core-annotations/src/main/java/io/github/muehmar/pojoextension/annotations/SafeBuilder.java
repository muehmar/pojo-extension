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
  String extensionName() default "ASD";
}
