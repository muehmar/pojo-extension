package io.github.muehmar.pojoextension.generator.impl.gen;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import java.util.function.BiFunction;

public class MethodGenerator<A, B> implements Generator<A, B> {
  private final BiFunction<A, B, JavaModifiers> createModifiers;
  private final BiFunction<A, B, String> createReturnType;
  private final BiFunction<A, B, String> createMethodName;
  private final BiFunction<A, B, PList<String>> createArguments;
  private final Generator<A, B> contentGenerator;

  public MethodGenerator(
      BiFunction<A, B, JavaModifiers> createModifiers,
      BiFunction<A, B, String> createReturnType,
      BiFunction<A, B, String> createMethodName,
      BiFunction<A, B, PList<String>> createArguments,
      Generator<A, B> contentGenerator) {
    this.createModifiers = createModifiers;
    this.createReturnType = createReturnType;
    this.createMethodName = createMethodName;
    this.createArguments = createArguments;
    this.contentGenerator = contentGenerator;
  }

  @Override
  public Writer generate(A data, B settings, Writer writer) {
    return Generator.<A, B>ofWriterFunction(
            w -> {
              final JavaModifiers modifiers = createModifiers.apply(data, settings);
              final String returnType = createReturnType.apply(data, settings);
              final String methodName = createMethodName.apply(data, settings);
              final String arguments = createArguments.apply(data, settings).mkString(", ");
              return w.print(
                  "%s%s %s(%s) {",
                  modifiers.asStringTrailingWhitespace(), returnType, methodName, arguments);
            })
        .append(1, contentGenerator)
        .append(w -> w.println("}"))
        .generate(data, settings, writer);
  }
}
