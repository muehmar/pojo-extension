package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.FullBuilderField;

public class CompleteSafeBuilderGens {

  private CompleteSafeBuilderGens() {}

  public static Generator<Pojo, PojoSettings> completeSafeBuilder() {
    return NormalBuilderGens.builderClass()
        .append(newLine())
        .appendList(
            SafeBuilderGens.fieldBuilderClass().append(newLine()),
            FullBuilderField::requiredFromPojo)
        .append(SafeBuilderGens.finalRequiredBuilder())
        .append(newLine())
        .appendList(
            SafeBuilderGens.fieldBuilderClass().append(newLine()),
            FullBuilderField::optionalFromPojo)
        .append(SafeBuilderGens.finalOptionalBuilder())
        .filter((p, s) -> s.getSafeBuilderAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> newBuilderMethod() {
    return MethodGenBuilder.<Pojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .genericTypes(Pojo::getGenericTypeDeclarations)
        .returnType(p -> "Builder0" + p.getTypeVariablesSection())
        .methodName("newBuilder")
        .noArguments()
        .content(
            p ->
                String.format(
                    "return new Builder0%s(new Builder%s());",
                    p.getDiamond(), p.getTypeVariablesSection()))
        .build()
        .filter((p, s) -> s.getSafeBuilderAbility().isEnabled())
        .append(RefsGen.genericRefs());
  }
}
