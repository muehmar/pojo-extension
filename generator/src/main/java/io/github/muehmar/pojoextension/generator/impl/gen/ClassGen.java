package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import java.util.function.BiFunction;

public class ClassGen<A, B> implements Generator<A, B> {
  private final ClassType type;
  private final Generator<A, B> packageGen;
  private final JavaModifiers modifiers;
  private final BiFunction<A, B, String> createClassName;
  private final PList<Generator<A, B>> content;

  private ClassGen(
      ClassType type,
      Generator<A, B> packageGen,
      JavaModifiers modifiers,
      BiFunction<A, B, String> createClassName,
      PList<Generator<A, B>> content) {
    this.type = type;
    this.packageGen = packageGen;
    this.modifiers = modifiers;
    this.createClassName = createClassName;
    this.content = content;
  }

  @Override
  public Writer generate(A data, B settings, Writer writer) {

    final Generator<A, B> contentGenerator =
        content.reduce(Generator::append).orElse((d, s, w) -> w);

    return packageGen()
        .append(this::refs)
        .append(this::classStart)
        .append(1, contentGenerator)
        .append(this::classEnd)
        .generate(data, settings, writer);
  }

  private Generator<A, B> packageGen() {
    if (type.equals(ClassType.NESTED)) {
      return Generator.emptyGen();
    }

    return packageGen.append(newLine());
  }

  private Writer refs(A data, B settings, Writer writer) {
    if (type.equals(ClassType.NESTED)) {
      return writer;
    }

    return writer.printRefs().println();
  }

  private Writer classStart(A data, B settings, Writer writer) {
    return writer.println(
        "%sclass %s {",
        modifiers.asStringTrailingWhitespace(), createClassName.apply(data, settings));
  }

  private Writer classEnd(A data, B settings, Writer writer) {
    return writer.println("}");
  }

  public static <A, B> Builder1<A, B> topLevel() {
    return new Builder1<>(ClassType.TOP_LEVEL);
  }

  public static <A, B> Builder2<A, B> nested() {
    return new Builder2<>(ClassType.NESTED, Generator.emptyGen());
  }

  public static final class Builder1<A, B> {
    private final ClassType type;

    private Builder1(ClassType type) {
      this.type = type;
    }

    public Builder2<A, B> packageGen(Generator<A, B> packageGen) {
      return new Builder2<>(type, packageGen);
    }
  }

  public static final class Builder2<A, B> {
    private final ClassType type;
    private final Generator<A, B> packageGen;

    public Builder2(ClassType type, Generator<A, B> packageGen) {
      this.type = type;
      this.packageGen = packageGen;
    }

    public Builder3<A, B> modifiers(JavaModifier... modifiers) {
      return new Builder3<>(type, packageGen, JavaModifiers.of(modifiers));
    }
  }

  public static final class Builder3<A, B> {
    private final ClassType type;
    private final Generator<A, B> packageGen;
    private final JavaModifiers modifiers;

    public Builder3(ClassType type, Generator<A, B> packageGen, JavaModifiers modifiers) {
      this.type = type;
      this.packageGen = packageGen;
      this.modifiers = modifiers;
    }

    public Builder4<A, B> className(BiFunction<A, B, String> createClassName) {
      return new Builder4<>(type, packageGen, modifiers, createClassName);
    }

    public Builder4<A, B> className(String className) {
      return className((data, settings) -> className);
    }
  }

  public static final class Builder4<A, B> {
    private final ClassType type;
    private final Generator<A, B> packageGen;
    private final JavaModifiers modifiers;
    private final BiFunction<A, B, String> createClassName;

    public Builder4(
        ClassType type,
        Generator<A, B> packageGen,
        JavaModifiers modifiers,
        BiFunction<A, B, String> createClassName) {
      this.type = type;
      this.packageGen = packageGen;
      this.modifiers = modifiers;
      this.createClassName = createClassName;
    }

    @SafeVarargs
    public final ClassGen<A, B> content(Generator<A, B>... generators) {
      return new ClassGen<>(
          type, packageGen, modifiers, createClassName, PList.fromArray(generators));
    }
  }

  public enum ClassType {
    TOP_LEVEL,
    NESTED
  }
}
