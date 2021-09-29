package io.github.muehmar.pojoextension.generator.impl;

import io.github.muehmar.pojoextension.generator.Writer;

public class WriterFactory {
  private WriterFactory() {}

  public static Writer createDefault() {
    return WriterImpl.createDefault();
  }

  public static Writer ofSpacesPerIndent(int spacesPerIndent) {
    return WriterImpl.ofSpacesPerIndent(spacesPerIndent);
  }
}
