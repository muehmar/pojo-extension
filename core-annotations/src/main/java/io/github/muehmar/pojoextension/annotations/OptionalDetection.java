package io.github.muehmar.pojoextension.annotations;

import java.util.Optional;
import java.util.stream.Stream;

/** Defines how optional fields in a pojo are detected. */
public enum OptionalDetection {

  /**
   * No field is considered as optional. If used in combination with another value, it will get
   * ignored.
   */
  NONE,

  /** A field is considered as optional in case the type is {@link Optional}. */
  OPTIONAL_CLASS,

  /** A field is considered as optional in case it is annotated with {@link Nullable}. */
  NULLABLE_ANNOTATION;

  public static Optional<OptionalDetection> fromString(String name) {
    return Stream.of(values()).filter(od -> od.name().equals(name)).findFirst();
  }
}
