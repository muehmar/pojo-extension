package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ClassGen<A, B> implements Generator<A, B> {
  private final ClassType type;
  private final Declaration declaration;
  private final Generator<A, B> packageGen;
  private final JavaModifiers modifiers;
  private final BiFunction<A, B, String> createClassName;
  private final BiFunction<A, B, Optional<String>> superClass;
  private final BiFunction<A, B, PList<String>> interfaces;
  private final PList<Generator<A, B>> content;

  private ClassGen(
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

  public static <A, B> DeclarationBuilder<A, B> ifc() {
    return new DeclarationBuilder<>(ClassType.INTERFACE);
  }

  public static <A, B> DeclarationBuilder<A, B> clazz() {
    return new DeclarationBuilder<>(ClassType.CLASS);
  }

  public static final class DeclarationBuilder<A, B> {
    private final ClassType type;

    public DeclarationBuilder(ClassType type) {
      this.type = type;
    }

    public Builder1<A, B> topLevel() {
      return new Builder1<>(type, Declaration.TOP_LEVEL);
    }

    public Builder2<A, B> nested() {
      return new Builder2<>(type, Declaration.NESTED, Generator.emptyGen());
    }
  }

  public static final class Builder1<A, B> {
    private final ClassType type;
    private final Declaration declaration;

    private Builder1(ClassType type, Declaration declaration) {
      this.type = type;
      this.declaration = declaration;
    }

    public Builder2<A, B> packageGen(Generator<A, B> packageGen) {
      return new Builder2<>(type, declaration, packageGen);
    }
  }

  public static final class Builder2<A, B> {
    private final ClassType type;
    private final Declaration declaration;
    private final Generator<A, B> packageGen;

    public Builder2(ClassType type, Declaration declaration, Generator<A, B> packageGen) {
      this.type = type;
      this.declaration = declaration;
      this.packageGen = packageGen;
    }

    public Builder3<A, B> modifiers(JavaModifier... modifiers) {
      return new Builder3<>(type, declaration, packageGen, JavaModifiers.of(modifiers));
    }
  }

  public static final class Builder3<A, B> {
    private final ClassType type;
    private final Declaration declaration;
    private final Generator<A, B> packageGen;
    private final JavaModifiers modifiers;

    public Builder3(
        ClassType type,
        Declaration declaration,
        Generator<A, B> packageGen,
        JavaModifiers modifiers) {
      this.type = type;
      this.declaration = declaration;
      this.packageGen = packageGen;
      this.modifiers = modifiers;
    }

    public SuperClassBuilder<A, B> className(BiFunction<A, B, String> createClassName) {
      return new SuperClassBuilder<>(type, declaration, packageGen, modifiers, createClassName);
    }

    public SuperClassBuilder<A, B> className(Function<A, String> className) {
      return className((data, settings) -> className.apply(data));
    }

    public SuperClassBuilder<A, B> className(String className) {
      return className((data, settings) -> className);
    }
  }

  public static final class SuperClassBuilder<A, B> {
    private final ClassType type;
    private final Declaration declaration;
    private final Generator<A, B> packageGen;
    private final JavaModifiers modifiers;
    private final BiFunction<A, B, String> createClassName;

    public SuperClassBuilder(
        ClassType type,
        Declaration declaration,
        Generator<A, B> packageGen,
        JavaModifiers modifiers,
        BiFunction<A, B, String> createClassName) {
      this.type = type;
      this.declaration = declaration;
      this.packageGen = packageGen;
      this.modifiers = modifiers;
      this.createClassName = createClassName;
    }

    public InterfacesBuilder<A, B> noSuperClass() {
      return superClassInt((data, settings) -> Optional.empty());
    }

    public ContentBuilder<A, B> noSuperClassAndInterface() {
      return new ContentBuilder<>(
          type,
          declaration,
          packageGen,
          modifiers,
          createClassName,
          (d, s) -> Optional.<String>empty(),
          (d, s) -> PList.<String>empty());
    }

    private InterfacesBuilder<A, B> superClassInt(BiFunction<A, B, Optional<String>> superClass) {
      return new InterfacesBuilder<>(
          type, declaration, packageGen, modifiers, createClassName, superClass);
    }

    public InterfacesBuilder<A, B> superClass(BiFunction<A, B, Name> superClass) {
      return superClassInt((d, s) -> Optional.of(superClass.apply(d, s).asString()));
    }

    public InterfacesBuilder<A, B> superClass(Function<A, String> superClass) {
      return superClassInt((data, settings) -> Optional.of(superClass.apply(data)));
    }
  }

  public static final class InterfacesBuilder<A, B> {
    private final ClassType type;
    private final Declaration declaration;
    private final Generator<A, B> packageGen;
    private final JavaModifiers modifiers;
    private final BiFunction<A, B, String> createClassName;
    private final BiFunction<A, B, Optional<String>> superClass;

    public InterfacesBuilder(
        ClassType type,
        Declaration declaration,
        Generator<A, B> packageGen,
        JavaModifiers modifiers,
        BiFunction<A, B, String> createClassName,
        BiFunction<A, B, Optional<String>> superClass) {
      this.type = type;
      this.declaration = declaration;
      this.packageGen = packageGen;
      this.modifiers = modifiers;
      this.createClassName = createClassName;
      this.superClass = superClass;
    }

    public ContentBuilder<A, B> doesNotImplementInterfaces() {
      return interfaces((d, s) -> PList.empty());
    }

    public ContentBuilder<A, B> interfaces(BiFunction<A, B, PList<String>> interfaces) {
      return new ContentBuilder<>(
          type, declaration, packageGen, modifiers, createClassName, superClass, interfaces);
    }

    public ContentBuilder<A, B> singleInterface(Function<A, String> interfaceName) {
      return interfaces((data, settings) -> PList.single(interfaceName.apply(data)));
    }

    public ContentBuilder<A, B> singleInterface(BiFunction<A, B, String> interfaceName) {
      return interfaces((data, settings) -> PList.single(interfaceName.apply(data, settings)));
    }
  }

  public static final class ContentBuilder<A, B> {
    private final ClassType type;
    private final Declaration declaration;
    private final Generator<A, B> packageGen;
    private final JavaModifiers modifiers;
    private final BiFunction<A, B, String> createClassName;
    private final BiFunction<A, B, Optional<String>> superClass;
    private final BiFunction<A, B, PList<String>> interfaces;

    public ContentBuilder(
        ClassType type,
        Declaration declaration,
        Generator<A, B> packageGen,
        JavaModifiers modifiers,
        BiFunction<A, B, String> createClassName,
        BiFunction<A, B, Optional<String>> superClass,
        BiFunction<A, B, PList<String>> interfaces) {
      this.type = type;
      this.declaration = declaration;
      this.packageGen = packageGen;
      this.modifiers = modifiers;
      this.createClassName = createClassName;
      this.superClass = superClass;
      this.interfaces = interfaces;
    }

    @SafeVarargs
    public final ClassGen<A, B> content(Generator<A, B>... generators) {
      return new ClassGen<>(
          type,
          declaration,
          packageGen,
          modifiers,
          createClassName,
          superClass,
          interfaces,
          PList.fromArray(generators));
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
