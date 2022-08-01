package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.BuilderField;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;

public class CompleteSafeBuilderGens {

  private CompleteSafeBuilderGens() {}

  public static Generator<Pojo, PojoSettings> completeSafeBuilder() {
    return NormalBuilderGens.builderClass()
        .append(newLine())
        .appendList(
            SafeBuilderGens.fieldBuilderClass().append(newLine()), BuilderField::requiredFromPojo)
        .append(SafeBuilderGens.finalRequiredBuilder())
        .append(newLine())
        .appendList(
            SafeBuilderGens.fieldBuilderClass().append(newLine()), BuilderField::optionalFromPojo)
        .append(SafeBuilderGens.finalOptionalBuilder())
        .filter((p, s) -> s.getSafeBuilderAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> newBuilderMethod() {
    return JavaGenerators.<Pojo, PojoSettings>methodGen()
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
