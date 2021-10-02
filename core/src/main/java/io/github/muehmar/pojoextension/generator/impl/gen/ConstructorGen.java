package io.github.muehmar.pojoextension.generator.impl.gen;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ConstructorGen<A, B> implements Generator<A, B> {
  private final BiFunction<A, B, JavaModifiers> createModifiers;
  private final BiFunction<A, B, String> createClassName;
  private final BiFunction<A, B, PList<String>> createArguments;
  private final Generator<A, B> contentGenerator;

  private ConstructorGen(
      BiFunction<A, B, JavaModifiers> createModifiers,
      BiFunction<A, B, String> createClassName,
      BiFunction<A, B, PList<String>> createArguments,
      Generator<A, B> contentGenerator) {
    this.createModifiers = createModifiers;
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
              final JavaModifiers modifiers = createModifiers.apply(data, settings);
              return w.print(
                  "%s%s(%s) {", modifiers.asStringTrailingWhitespace(), className, arguments);
            })
        .append(contentGenerator, 1)
        .append(w -> w.println("}"))
        .generate(data, settings, writer);
  }

  public static <A, B> Builder1<A, B> modifiers(JavaModifier... modifiers) {
    return new Builder1<>((d, s) -> JavaModifiers.of(modifiers));
  }

  public static <A, B> Builder1<A, B> modifiers(BiFunction<A, B, JavaModifiers> createModifiers) {
    return new Builder1<>(createModifiers);
  }

  public static class Builder1<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;

    private Builder1(BiFunction<A, B, JavaModifiers> createModifiers) {
      this.createModifiers = createModifiers;
    }

    public Builder2<A, B> className(BiFunction<A, B, String> createClassName) {
      return new Builder2<>(createModifiers, createClassName);
    }

    public Builder2<A, B> className(Function<A, String> createClassName) {
      return className((data, settings) -> createClassName.apply(data));
    }

    public Builder2<A, B> className(String className) {
      return className((data, settings) -> className);
    }
  }

  public static class Builder2<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;
    private final BiFunction<A, B, String> createClassName;

    private Builder2(
        BiFunction<A, B, JavaModifiers> createModifiers, BiFunction<A, B, String> createClassName) {
      this.createModifiers = createModifiers;
      this.createClassName = createClassName;
    }

    public Builder3<A, B> arguments(BiFunction<A, B, PList<String>> createArguments) {
      return new Builder3<>(createModifiers, createClassName, createArguments);
    }

    public Builder3<A, B> arguments(Function<A, PList<String>> createArguments) {
      return arguments((data, settings) -> createArguments.apply(data));
    }

    public Builder3<A, B> singleArgument(Function<A, String> createArgument) {
      return arguments((data, settings) -> PList.single(createArgument.apply(data)));
    }

    public Builder3<A, B> singleArgument(String argument) {
      return arguments((data, settings) -> PList.single(argument));
    }

    public Builder3<A, B> noArguments() {
      return arguments((data, settings) -> PList.empty());
    }
  }

  public static class Builder3<A, B> {
    private final BiFunction<A, B, JavaModifiers> createModifiers;
    private final BiFunction<A, B, String> createClassName;
    private final BiFunction<A, B, PList<String>> createArguments;

    private Builder3(
        BiFunction<A, B, JavaModifiers> createModifiers,
        BiFunction<A, B, String> createClassName,
        BiFunction<A, B, PList<String>> createArguments) {
      this.createModifiers = createModifiers;
      this.createClassName = createClassName;
      this.createArguments = createArguments;
    }

    public ConstructorGen<A, B> content(String content) {
      return content((data, settings, writer) -> writer.println(content));
    }

    public ConstructorGen<A, B> content(Generator<A, B> content) {
      return new ConstructorGen<>(createModifiers, createClassName, createArguments, content);
    }

    public ConstructorGen<A, B> content(UnaryOperator<Writer> content) {
      return content((data, settings, writer) -> content.apply(writer));
    }
  }
}
