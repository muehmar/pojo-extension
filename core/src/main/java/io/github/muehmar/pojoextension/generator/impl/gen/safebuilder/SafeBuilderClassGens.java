package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;

public class SafeBuilderClassGens {
  private SafeBuilderClassGens() {}

  public static Generator<Pojo, PojoSettings> safeBuilderClass() {
    return ClassGen.<Pojo, PojoSettings>topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, FINAL)
        .className((p, s) -> s.builderName(p).asString())
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

  private static Generator<Pojo, PojoSettings> createMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("Builder0")
        .methodName("create")
        .noArguments()
        .content("return new Builder0(new Builder());");
  }
}
