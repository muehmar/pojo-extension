package io.github.muehmar.pojoextension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Specifies which field of a pojo is optional. */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Nullable {}
