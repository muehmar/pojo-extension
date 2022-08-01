package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.codegenerator.Generator;

public class Annotations {
  private Annotations() {}

  public static <A, B> Generator<A, B> overrideAnnotation() {
    return Generator.ofWriterFunction(w -> w.println("@Override"));
  }
}
