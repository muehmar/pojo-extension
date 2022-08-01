package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class Generators {
  private Generators() {}

  public static <A, B> Generator<A, B> newLine() {
    return Generator.ofWriterFunction(Writer::println);
  }
}
