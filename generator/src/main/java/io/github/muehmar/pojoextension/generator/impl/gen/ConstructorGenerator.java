package io.github.muehmar.pojoextension.generator.impl.gen;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import java.util.function.BiFunction;

public class ConstructorGenerator<A, B> implements Generator<A, B> {
  private final JavaModifiers modifiers;
  private final BiFunction<A, B, String> createClassName;
  private final BiFunction<A, B, PList<String>> createArguments;
  private final Generator<A, B> contentGenerator;

  public ConstructorGenerator(
      JavaModifiers modifiers,
      BiFunction<A, B, String> createClassName,
      BiFunction<A, B, PList<String>> createArguments,
      Generator<A, B> contentGenerator) {
    this.modifiers = modifiers;
    this.createClassName = createClassName;
    this.createArguments = createArguments;
    this.contentGenerator = contentGenerator;
  }

  @Override
  public Writer generate(A data, B settings, Writer writer) {
    return Generator.<A, B>ofWriterFunction(
            w -> {
              final String arguments = createArguments.apply(data, settings).mkString(", ");
              final String className = createClassName.apply(data, settings);
              return w.print(
                  "%s%s(%s) {", modifiers.asStringTrailingWhitespace(), className, arguments);
            })
        .append(1, contentGenerator)
        .append(w -> w.println("}"))
        .generate(data, settings, writer);
  }
}
