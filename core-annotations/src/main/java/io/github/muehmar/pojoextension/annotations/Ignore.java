package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ignores an annotated field. This can be used particularly for fields which are instantiated in a
 * constructor and are not passed as argument to the constructor.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Ignore {}
