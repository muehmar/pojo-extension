package io.github.muehmar.pojoextension.annotations;

import java.util.Optional;

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
  NULLABLE_ANNOTATION
}
