package io.github.muehmar.pojoextension.generator.impl.gen;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class MethodGen<A, B> implements Generator<A, B> {
  private final BiFunction<A, B, JavaModifiers> createModifiers;
  private final BiFunction<A, B, String> createReturnType;
  private final BiFunction<A, B, String> createMethodName;
  private final BiFunction<A, B, PList<String>> createArguments;
  private final Generator<A, B> contentGenerator;

  public MethodGen(
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
        .append(contentGenerator, 1)
        .append(w -> w.println("}"))
        .generate(data, settings, writer);
  }

  public static <A, B> Builder1<A, B> modifiers(JavaModifier... modifiers) {
    return modifiers((d, s) -> JavaModifiers.of(modifiers));
  }

  public static <A, B> Builder1<A, B> modifiers(BiFunction<A, B, JavaModifiers> createModifiers) {
    return new Builder1<>(createModifiers);
  }

  public static class Builder1<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;

    private Builder1(BiFunction<A, B, JavaModifiers> createModifiers) {
      this.createModifiers = createModifiers;
    }

    public Builder2<A, B> returnType(BiFunction<A, B, String> createReturnType) {
      return new Builder2<>(createModifiers, createReturnType);
    }

    public Builder2<A, B> returnType(Function<A, String> createReturnType) {
      return returnType((data, settings) -> createReturnType.apply(data));
    }

    public Builder2<A, B> returnType(String returnType) {
      return returnType((data, settings) -> returnType);
    }
  }

  public static class Builder2<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;
    private final BiFunction<A, B, String> createReturnType;

    private Builder2(
        BiFunction<A, B, JavaModifiers> createModifiers,
        BiFunction<A, B, String> createReturnType) {
      this.createModifiers = createModifiers;
      this.createReturnType = createReturnType;
    }

    public Builder3<A, B> methodName(BiFunction<A, B, String> createMethodName) {
      return new Builder3<>(createModifiers, createReturnType, createMethodName);
    }

    public Builder3<A, B> methodName(Function<A, String> createMethodName) {
      return methodName((data, settings) -> createMethodName.apply(data));
    }

    public Builder3<A, B> methodName(String methodName) {
      return methodName((data, settings) -> methodName);
    }
  }

  public static class Builder3<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;
    private final BiFunction<A, B, String> createReturnType;
    private final BiFunction<A, B, String> createMethodName;

    private Builder3(
        BiFunction<A, B, JavaModifiers> createModifiers,
        BiFunction<A, B, String> createReturnType,
        BiFunction<A, B, String> createMethodName) {
      this.createModifiers = createModifiers;
      this.createReturnType = createReturnType;
      this.createMethodName = createMethodName;
    }

    public Builder4<A, B> arguments(BiFunction<A, B, PList<String>> createArguments) {
      return new Builder4<>(createModifiers, createReturnType, createMethodName, createArguments);
    }

    public Builder4<A, B> arguments(Function<A, PList<String>> createArguments) {
      return arguments((data, settings) -> createArguments.apply(data));
    }

    public Builder4<A, B> singleArgument(Function<A, String> createArgument) {
      return arguments((data, settings) -> PList.single(createArgument.apply(data)));
    }

    public Builder4<A, B> noArguments() {
      return arguments((data, settings) -> PList.empty());
    }
  }

  public static class Builder4<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;
    private final BiFunction<A, B, String> createReturnType;
    private final BiFunction<A, B, String> createMethodName;
    private final BiFunction<A, B, PList<String>> createArguments;

    private Builder4(
        BiFunction<A, B, JavaModifiers> createModifiers,
        BiFunction<A, B, String> createReturnType,
        BiFunction<A, B, String> createMethodName,
        BiFunction<A, B, PList<String>> createArguments) {
      this.createModifiers = createModifiers;
      this.createReturnType = createReturnType;
      this.createMethodName = createMethodName;
      this.createArguments = createArguments;
    }

    public MethodGen<A, B> content(String content) {
      return content((data, settings, writer) -> writer.println(content));
    }

    public MethodGen<A, B> content(Generator<A, B> content) {
      return new MethodGen<>(
          createModifiers, createReturnType, createMethodName, createArguments, content);
    }

    public MethodGen<A, B> content(UnaryOperator<Writer> content) {
      return content((data, settings, writer) -> content.apply(writer));
    }
  }
}
