package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@SafeBuilder
public class ClassGen<A, B> implements Generator<A, B> {
  private final ClassType type;
  private final Declaration declaration;
  private final Generator<A, B> packageGen;
  private final JavaModifiers modifiers;
  private final BiFunction<A, B, String> createClassName;
  private final BiFunction<A, B, Optional<String>> superClass;
  private final BiFunction<A, B, PList<String>> interfaces;
  private final PList<Generator<A, B>> content;

  @SuppressWarnings("java:S107")
  ClassGen(
      ClassType type,
      Declaration declaration,
      Generator<A, B> packageGen,
      JavaModifiers modifiers,
      BiFunction<A, B, String> createClassName,
      BiFunction<A, B, Optional<String>> superClass,
      BiFunction<A, B, PList<String>> interfaces,
      PList<Generator<A, B>> content) {
    this.type = type;
    this.declaration = declaration;
    this.packageGen = packageGen;
    this.modifiers = modifiers;
    this.createClassName = createClassName;
    this.superClass = superClass;
    this.interfaces = interfaces;
    this.content = content;
  }

  @Override
  public Writer generate(A data, B settings, Writer writer) {

    final Generator<A, B> contentGenerator =
        content.reduce(Generator::append).orElse((d, s, w) -> w);

    return packageGen()
        .append(this::refs)
        .append(this::classStart)
        .append(contentGenerator, 1)
        .append(this::classEnd)
        .generate(data, settings, writer);
  }

  private Generator<A, B> packageGen() {
    if (declaration.equals(Declaration.NESTED)) {
      return Generator.emptyGen();
    }

    return packageGen.append(newLine());
  }

  private Writer refs(A data, B settings, Writer writer) {
    if (declaration.equals(Declaration.NESTED)) {
      return writer;
    }

    return writer.printRefs().println();
  }

  private Writer classStart(A data, B settings, Writer writer) {
    final String superClassStr =
        Strings.surroundIfNotEmpty(" extends ", superClass.apply(data, settings).orElse(""), "");
    final String interfacesStr =
        Strings.surroundIfNotEmpty(
            " implements ", interfaces.apply(data, settings).mkString(", "), "");
    return writer.println(
        "%s%s %s%s%s {",
        modifiers.asStringTrailingWhitespace(),
        type.value,
        createClassName.apply(data, settings),
        superClassStr,
        interfacesStr);
  }

  private Writer classEnd(A data, B settings, Writer writer) {
    return writer.println("}");
  }

  @FieldBuilder(fieldName = "type")
  static class TypeBuilder {
    private TypeBuilder() {}

    static ClassType ifc() {
      return ClassType.INTERFACE;
    }

    static ClassType clazz() {
      return ClassType.CLASS;
    }
  }

  @FieldBuilder(fieldName = "declaration")
  static class DeclarationBuilder1 {
    private DeclarationBuilder1() {}

    static Declaration topLevel() {
      return Declaration.TOP_LEVEL;
    }

    static Declaration nested() {
      return Declaration.NESTED;
    }
  }

  @FieldBuilder(fieldName = "packageGen")
  static class PackageBuilder {
    private PackageBuilder() {}
  }

  @FieldBuilder(fieldName = "modifiers")
  static class ModifierBuilder {
    private ModifierBuilder() {}

    static JavaModifiers noModifiers() {
      return JavaModifiers.empty();
    }

    static JavaModifiers modifiers(JavaModifier m1) {
      return JavaModifiers.of(m1);
    }

    static JavaModifiers modifiers(JavaModifier m1, JavaModifier m2) {
      return JavaModifiers.of(m1, m2);
    }

    static JavaModifiers modifiers(JavaModifier m1, JavaModifier m2, JavaModifier m3) {
      return JavaModifiers.of(m1, m2, m3);
    }

    static JavaModifiers modifiers(PList<JavaModifier> modifiers) {
      return JavaModifiers.of(modifiers);
    }
  }

  @FieldBuilder(fieldName = "createClassName")
  static class ClassNameBuilder {
    private ClassNameBuilder() {}

    static <A, B> BiFunction<A, B, String> className(BiFunction<A, B, String> createClassName) {
      return createClassName;
    }

    static <A, B> BiFunction<A, B, String> className(Function<A, String> className) {
      return (data, settings) -> className.apply(data);
    }

    static <A, B> BiFunction<A, B, String> className(String className) {
      return (data, settings) -> className;
    }
  }

  @FieldBuilder(fieldName = "superClass")
  static class SuperClassBuilder1 {
    private SuperClassBuilder1() {}

    static <A, B> BiFunction<A, B, Optional<String>> noSuperClass() {
      return (data, settings) -> Optional.empty();
    }

    static <A, B> BiFunction<A, B, Optional<String>> superClassName(
        BiFunction<A, B, Name> superClass) {
      return (data, settings) -> Optional.of(superClass.apply(data, settings).asString());
    }
  }

  @FieldBuilder(fieldName = "interfaces")
  static class InterfacesBuilder1 {
    private InterfacesBuilder1() {}

    static <A, B> BiFunction<A, B, PList<String>> noInterfaces() {
      return (data, settings) -> PList.empty();
    }

    static <A, B> BiFunction<A, B, PList<String>> singleInterface(
        BiFunction<A, B, String> interfaceName) {
      return (data, settings) -> PList.of(interfaceName.apply(data, settings));
    }
  }

  @FieldBuilder(fieldName = "content")
  static class ContentBuilder1 {
    private ContentBuilder1() {}

    static <A, B> PList<Generator<A, B>> noContent() {
      return PList.empty();
    }

    static <A, B> PList<Generator<A, B>> content(Generator<A, B> c1) {
      return PList.single(c1);
    }

    static <A, B> PList<Generator<A, B>> content(Generator<A, B> c1, Generator<A, B> c2) {
      return PList.of(c1, c2);
    }
  }

  public enum Declaration {
    TOP_LEVEL,
    NESTED
  }

  public enum ClassType {
    CLASS("class"),
    INTERFACE("interface");

    private final String value;

    ClassType(String value) {
      this.value = value;
    }
  }
}
