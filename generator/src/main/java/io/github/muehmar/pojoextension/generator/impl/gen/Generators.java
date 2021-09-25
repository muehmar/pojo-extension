package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;

public class Generators {
  private Generators() {}

  public static <A, B> Generator<A, B> newLine() {
    return Generator.ofWriterFunction(Writer::println);
  }
}
