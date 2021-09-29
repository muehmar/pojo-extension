package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static io.github.muehmar.pojoextension.generator.Generator.emptyGen;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.FieldDeclarationGen;

public class BuilderGen implements Generator<Pojo, PojoSettings> {
  @Override
  public Writer generate(Pojo pojo, PojoSettings settings, Writer writer) {
    final Generator<Pojo, PojoSettings> contentGen =
        new ConstructorGen<Pojo, PojoSettings>(
                JavaModifiers.of(PRIVATE), (p, s) -> "Builder", (p, s) -> PList.empty(), emptyGen())
            .append(newLine())
            .appendList(FieldDeclarationGen.ofModifiers(PRIVATE), Pojo::getFields)
            .append(newLine())
            .appendList(new SetMethodGen().append(newLine()), Pojo::getFields)
            .appendList(
                new SetMethodOptionalGen().append(newLine()),
                p -> p.getFields().filter(PojoField::isOptional))
            .append(new BuildMethodGen());

    return ClassGen.<Pojo, PojoSettings>nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .createClassName((p, s) -> "Builder")
        .content(contentGen)
        .generate(pojo, settings, writer);
  }
}
