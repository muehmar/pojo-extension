package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Generic;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import java.util.function.Function;

public class SafeBuilderClassGens {
  private SafeBuilderClassGens() {}

  public static Generator<Pojo, PojoSettings> safeBuilderClass() {
    return ClassGen.<Pojo, PojoSettings>clazz()
        .topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, FINAL)
        .className((p, s) -> s.builderName(p).asString())
        .noSuperClassAndInterface()
        .content(content());
  }

  private static Generator<Pojo, PojoSettings> content() {
    final ConstructorGen<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className((p, s) -> s.builderName(p).asString())
            .noArguments()
            .content("");
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
    return MethodGenBuilder.<Pojo, PojoSettings>create()
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
