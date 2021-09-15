package io.github.muehmar.pojoextension.generator;

public class GeneratorFactory {
  private GeneratorFactory() {}

  public static Generator create() {
    final JavaGenerator javaGenerator = new JavaGenerator(new JavaResolver());
    return (pojo, settings) -> {
      final WriterImpl writer = new WriterImpl();
      javaGenerator.generate(writer, pojo, settings);
      return writer.asString();
    };
  }
}
