package io.github.muehmar.pojoextension.generator.impl.gen.withers;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.DEFAULT;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Generic;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoAndField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.ConstructorCallGens;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.FieldVariable;
import java.util.function.Function;

public class WithGens {
  private WithGens() {}

  public static Generator<PojoAndField, PojoSettings> withMethod() {
    return MethodGen.<PojoAndField, PojoSettings>modifiers(DEFAULT)
        .noGenericTypes()
        .returnTypeName(paf -> paf.getPojo().getNameWithTypeVariables())
        .methodName(paf -> "with" + paf.getField().getName().toPascalCase())
        .singleArgument(
            paf ->
                String.format(
                    "%s %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
        .content(withMethodContent())
        .append(RefsGen.fieldRefs(), PojoAndField::getField)
        .filter((p, s) -> s.getWithersAbility().isEnabled());
  }

  private static Generator<PojoAndField, PojoSettings> withMethodContent() {
    return Generator.<PojoAndField, PojoSettings>emptyGen()
        .append(
            ConstructorCallGens.callWithSingleFieldVariable("return "),
            paf -> new FieldVariable(paf.getPojo(), paf.getField(), SAME_TYPE));
  }

  public static Generator<PojoAndField, PojoSettings> optionalWithMethod() {
    final Generator<PojoAndField, PojoSettings> method =
        MethodGen.<PojoAndField, PojoSettings>modifiers(DEFAULT)
            .noGenericTypes()
            .returnTypeName(paf -> paf.getPojo().getNameWithTypeVariables())
            .methodName(paf -> "with" + paf.getField().getName().toPascalCase())
            .singleArgument(
                paf ->
                    String.format(
                        "Optional<%s> %s",
                        paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
            .content(optionalWithMethodContent())
            .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
            .append(RefsGen.fieldRefs(), PojoAndField::getField);

    return Generator.<PojoAndField, PojoSettings>emptyGen()
        .appendConditionally(paf -> paf.getField().isOptional(), method)
        .filter((f, s) -> s.getWithersAbility().isEnabled());
  }

  public static Generator<PojoAndField, PojoSettings> staticOptionalWithMethod() {
    final Function<PojoAndField, PList<String>> arguments =
        paf ->
            PList.of(
                String.format("%s self", paf.getPojo().getNameWithTypeVariables()),
                String.format(
                    "Optional<%s> %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()));

    final Generator<PojoAndField, PojoSettings> method =
        MethodGen.<PojoAndField, PojoSettings>modifiers(PUBLIC, STATIC)
            .genericTypes(
                paf ->
                    paf.getPojo()
                        .getGenerics()
                        .map(Generic::getTypeDeclaration)
                        .map(Name::asString))
            .returnTypeName(paf -> paf.getPojo().getNameWithTypeVariables())
            .methodName(paf -> "with" + paf.getField().getName().toPascalCase())
            .arguments(arguments)
            .content(optionalWithMethodContent())
            .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
            .append(RefsGen.genericRefs(), PojoAndField::getPojo)
            .append(RefsGen.fieldRefs(), PojoAndField::getField);

    return Generator.<PojoAndField, PojoSettings>emptyGen()
        .appendConditionally(paf -> paf.getField().isOptional(), method)
        .filter((f, s) -> s.getWithersAbility().isEnabled());
  }

  private static Generator<PojoAndField, PojoSettings> optionalWithMethodContent() {
    return Generator.<PojoAndField, PojoSettings>emptyGen()
        .append(
            ConstructorCallGens.callWithSingleFieldVariable("return "),
            paf -> new FieldVariable(paf.getPojo(), paf.getField(), UNWRAP_OPTIONAL));
  }
}
