package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a method to be used as build method for the Builders. The actual built instance is passed
 * as argument to the method and it can return any type needed. An annotated method must be static
 * and can be package-private.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface BuildMethod {}
