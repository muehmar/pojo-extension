package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.SafeBuilderPojoField;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class CompleteSafeBuilderGens {
  private CompleteSafeBuilderGens() {}

  public static Generator<Pojo, PojoSettings> completeSafeBuilder() {
    return NormalBuilderGens.builderClass()
        .append(newLine())
        .appendList(
            SafeBuilderGens.fieldBuilderClass().append(newLine()),
            CompleteSafeBuilderGens::requiredPojoFields)
        .append(SafeBuilderGens.finalRequiredBuilder())
        .append(newLine())
        .appendList(
            SafeBuilderGens.fieldBuilderClass().append(newLine()),
            CompleteSafeBuilderGens::optionalPojoFields)
        .append(SafeBuilderGens.finalOptionalBuilder())
        .filter((p, s) -> s.getSafeBuilderAbility().isEnabled());
  }

  private static PList<SafeBuilderPojoField> requiredPojoFields(Pojo pojo) {
    return pojo.getFields()
        .filter(PojoField::isRequired)
        .zipWithIndex()
        .map(p -> new SafeBuilderPojoField(pojo, p.first(), p.second()));
  }

  private static PList<SafeBuilderPojoField> optionalPojoFields(Pojo pojo) {
    return pojo.getFields()
        .filter(PojoField::isOptional)
        .zipWithIndex()
        .map(p -> new SafeBuilderPojoField(pojo, p.first(), p.second()));
  }

  public static Generator<Pojo, PojoSettings> newBuilderMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .genericTypes(Pojo::getGenericTypeDeclarations)
        .returnType(p -> "Builder0" + p.getTypeVariablesSection())
        .methodName("newBuilder")
        .noArguments()
        .content(
            p ->
                String.format(
                    "return new Builder0%s(new Builder%s());", p.getDiamond(), p.getDiamond()))
        .filter((p, s) -> s.getSafeBuilderAbility().isEnabled())
        .append((p, s, w) -> p.getGenericImports().map(Name::asString).foldLeft(w, Writer::ref));
  }
}
