package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.model.Generic;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import java.util.function.Function;

public class SafeBuilderClassGens {
  private SafeBuilderClassGens() {}

  public static Generator<Pojo, PojoSettings> safeBuilderClass() {
    return JavaGenerators.<Pojo, PojoSettings>classGen()
        .clazz()
        .topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, FINAL)
        .className((p, s) -> s.builderName(p).asString())
        .noSuperClass()
        .noInterfaces()
        .content(content())
        .build();
  }

  private static Generator<Pojo, PojoSettings> content() {
    final Generator<Pojo, PojoSettings> constructor =
        JavaGenerators.<Pojo, PojoSettings>constructorGen()
            .modifiers(PRIVATE)
            .className((p, s) -> s.builderName(p).asString())
            .noArguments()
            .noContent()
            .build();
    return Generator.<Pojo, PojoSettings>emptyGen()
        .appendNewLine()
        .append(constructor)
        .appendNewLine()
        .append(createMethod())
        .appendNewLine()
        .append(CompleteSafeBuilderGens.completeSafeBuilder());
  }

  public static Generator<Pojo, PojoSettings> createMethod() {
    final Function<Pojo, String> returnType = p -> "Builder0" + p.getTypeVariablesSection();
    final Function<Pojo, String> content =
        p ->
            String.format(
                "return new Builder0%s(new Builder%s());",
                p.getDiamond(), p.getTypeVariablesSection());
    return JavaGenerators.<Pojo, PojoSettings>methodGen()
        .modifiers(PUBLIC, STATIC)
        .genericTypes(p -> p.getGenerics().map(Generic::getTypeDeclaration).map(Name::asString))
        .returnType(returnType)
        .methodName("create")
        .noArguments()
        .content(content)
        .build()
        .append(RefsGen.genericRefs());
  }
}
