package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;

public class ClassGenerator implements Generator<Pojo, PojoSettings> {
  private final PList<Generator<Pojo, PojoSettings>> content;

  public ClassGenerator(PList<Generator<Pojo, PojoSettings>> content) {
    this.content = content;
  }

  @SafeVarargs
  public static ClassGenerator ofContent(Generator<Pojo, PojoSettings>... generators) {
    return new ClassGenerator(PList.fromArray(generators));
  }

  @Override
  public Writer generate(Pojo pojo, PojoSettings settings, Writer writer) {

    final Generator<Pojo, PojoSettings> contentGenerator =
        content.reduce(Generator::append).orElse((p, s, w) -> w);

    return new PackageGenerator()
        .append(newLine())
        .append(Writer::printRefs)
        .append(newLine())
        .append(this::classStart)
        .append(1, contentGenerator)
        .append(this::classEnd)
        .generate(pojo, settings, writer);
  }

  private Writer classStart(Pojo pojo, PojoSettings settings, Writer writer) {
    return writer.println("public class %s {", pojo.getExtensionName().asString());
  }

  private Writer classEnd(Pojo pojo, PojoSettings settings, Writer writer) {
    return writer.println("}", pojo.getExtensionName().asString());
  }
}
