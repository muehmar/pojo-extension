package io.github.muehmar.pojoextension.generator.model.type;

public enum TypeKind {
  /** Declared type, i.e. class */
  DECLARED,

  /** Array */
  ARRAY,

  /** One of the eight primitves */
  PRIMITIVE,

  /** Type variable */
  TYPE_VAR
}
