package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import java.util.Comparator;

public class ClassGenerator implements Generator<Pojo, PojoSettings> {
  private final ClassType type;
  private final PList<JavaModifier> modifiers;
  private final PList<Generator<Pojo, PojoSettings>> content;

  private ClassGenerator(
      ClassType type, PList<JavaModifier> modifiers, PList<Generator<Pojo, PojoSettings>> content) {
    this.type = type;
    this.modifiers = modifiers;
    this.content = content;
  }

  @Override
  public Writer generate(Pojo pojo, PojoSettings settings, Writer writer) {

    final Generator<Pojo, PojoSettings> contentGenerator =
        content.reduce(Generator::append).orElse((p, s, w) -> w);

    return new PackageGenerator()
        .append(newLine())
        .append(this::refs)
        .append(this::classStart)
        .append(1, contentGenerator)
        .append(this::classEnd)
        .generate(pojo, settings, writer);
  }

  private Writer refs(Pojo pojo, PojoSettings settings, Writer writer) {
    if (type.equals(ClassType.NESTED)) {
      return writer;
    }

    return writer.printRefs().println();
  }

  private Writer classStart(Pojo pojo, PojoSettings settings, Writer writer) {
    return writer.println(
        "%sclass %s {",
        modifiers
            .sort(Comparator.comparingInt(JavaModifier::getOrder))
            .map(m -> String.format("%s ", m.asString()))
            .mkString(""),
        pojo.getExtensionName().asString());
  }

  private Writer classEnd(Pojo pojo, PojoSettings settings, Writer writer) {
    return writer.println("}", pojo.getExtensionName().asString());
  }

  public static ClassGeneratorCreator1 topLevel() {
    return new ClassGeneratorCreator1(ClassType.TOP_LEVEL);
  }

  public static ClassGeneratorCreator1 nested() {
    return new ClassGeneratorCreator1(ClassType.NESTED);
  }

  public static final class ClassGeneratorCreator1 {
    private final ClassType type;

    private ClassGeneratorCreator1(ClassType type) {
      this.type = type;
    }

    public ClassGeneratorCreator2 modifiers(JavaModifier... modifiers) {
      return new ClassGeneratorCreator2(type, PList.fromArray(modifiers));
    }
  }

  public static final class ClassGeneratorCreator2 {
    private final ClassType type;
    private final PList<JavaModifier> modifiers;

    private ClassGeneratorCreator2(ClassType type, PList<JavaModifier> modifiers) {
      this.type = type;
      this.modifiers = modifiers;
    }

    @SafeVarargs
    public final ClassGenerator content(Generator<Pojo, PojoSettings>... generators) {
      return new ClassGenerator(type, modifiers, PList.fromArray(generators));
    }
  }

  public enum ClassType {
    TOP_LEVEL,
    NESTED
  }
}
