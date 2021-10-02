package io.github.muehmar.pojoextension.generator.writer;

public class WriterFactory {
  private WriterFactory() {}

  public static Writer createDefault() {
    return WriterImpl.createDefault();
  }

  public static Writer ofSpacesPerIndent(int spacesPerIndent) {
    return WriterImpl.ofSpacesPerIndent(spacesPerIndent);
  }
}
