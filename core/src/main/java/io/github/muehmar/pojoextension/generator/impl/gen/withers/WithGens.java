package io.github.muehmar.pojoextension.generator.impl.gen.withers;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Generic;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.ConstructorCallGens;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.FieldVariable;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.data.WithField;
import java.util.function.Function;

public class WithGens {
  private WithGens() {}

  public static Generator<WithField, PojoSettings> withMethod() {
    return MethodGen.<WithField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(
            wf -> wf.getPojo().getName().asString() + wf.getPojo().getTypeVariablesSection())
        .methodName(wf -> "with" + wf.getField().getName().toPascalCase())
        .singleArgument(
            wf ->
                String.format(
                    "%s %s", wf.getField().getType().getTypeDeclaration(), wf.getField().getName()))
        .content(
            wf ->
                String.format(
                    "return with%s(self(), %s);",
                    wf.getField().getName().toPascalCase(), wf.getField().getName()))
        .append(RefsGen.fieldRefs(), WithField::getField)
        .filter((p, s) -> s.getWithersAbility().isEnabled());
  }

  public static Generator<WithField, PojoSettings> staticWithMethod() {
    final Function<WithField, PList<String>> arguments =
        wf ->
            PList.of(
                String.format(
                    "%s%s self", wf.getPojo().getName(), wf.getPojo().getTypeVariablesSection()),
                String.format(
                    "%s %s",
                    wf.getField().getType().getTypeDeclaration(), wf.getField().getName()));

    return MethodGen.<WithField, PojoSettings>modifiers(
            (p, s) -> JavaModifiers.of(s.getStaticMethodAccessModifier(), STATIC))
        .genericTypes(
            wf -> wf.getPojo().getGenerics().map(Generic::getTypeDeclaration).map(Name::asString))
        .returnType(
            wf -> wf.getPojo().getName().asString() + wf.getPojo().getTypeVariablesSection())
        .methodName(wf -> "with" + wf.getField().getName().toPascalCase())
        .arguments(arguments)
        .content(withMethodContent())
        .append(RefsGen.fieldRefs(), WithField::getField)
        .append(RefsGen.genericRefs(), WithField::getPojo)
        .filter((p, s) -> s.getWithersAbility().isEnabled());
  }

  private static Generator<WithField, PojoSettings> withMethodContent() {
    return Generator.<WithField, PojoSettings>emptyGen()
        .append(
            ConstructorCallGens.callWithSingleFieldVariable(),
            withField -> new FieldVariable(withField.getPojo(), withField.getField(), SAME_TYPE));
  }

  public static Generator<WithField, PojoSettings> optionalWithMethod() {
    final Generator<WithField, PojoSettings> method =
        MethodGen.<WithField, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(
                wf -> wf.getPojo().getName().asString() + wf.getPojo().getTypeVariablesSection())
            .methodName(wf -> "with" + wf.getField().getName().toPascalCase())
            .singleArgument(
                wf ->
                    String.format(
                        "Optional<%s> %s",
                        wf.getField().getType().getTypeDeclaration(), wf.getField().getName()))
            .content(
                wf ->
                    String.format(
                        "return with%s(self(), %s);",
                        wf.getField().getName().toPascalCase(), wf.getField().getName()))
            .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
            .append(RefsGen.fieldRefs(), WithField::getField);

    return Generator.<WithField, PojoSettings>emptyGen()
        .appendConditionally(wf -> wf.getField().isOptional(), method)
        .filter((f, s) -> s.getWithersAbility().isEnabled());
  }

  public static Generator<WithField, PojoSettings> staticOptionalWithMethod() {
    final Function<WithField, PList<String>> arguments =
        wf ->
            PList.of(
                String.format(
                    "%s%s self", wf.getPojo().getName(), wf.getPojo().getTypeVariablesSection()),
                String.format(
                    "Optional<%s> %s",
                    wf.getField().getType().getTypeDeclaration(), wf.getField().getName()));

    final Generator<WithField, PojoSettings> method =
        MethodGen.<WithField, PojoSettings>modifiers(PUBLIC, STATIC)
            .genericTypes(
                wf ->
                    wf.getPojo().getGenerics().map(Generic::getTypeDeclaration).map(Name::asString))
            .returnType(
                wf -> wf.getPojo().getName().asString() + wf.getPojo().getTypeVariablesSection())
            .methodName(wf -> "with" + wf.getField().getName().toPascalCase())
            .arguments(arguments)
            .content(optionalWithMethodContent())
            .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
            .append(RefsGen.genericRefs(), WithField::getPojo)
            .append(RefsGen.fieldRefs(), WithField::getField);

    return Generator.<WithField, PojoSettings>emptyGen()
        .appendConditionally(wf -> wf.getField().isOptional(), method)
        .filter((f, s) -> s.getWithersAbility().isEnabled());
  }

  private static Generator<WithField, PojoSettings> optionalWithMethodContent() {
    return Generator.<WithField, PojoSettings>emptyGen()
        .append(
            ConstructorCallGens.callWithSingleFieldVariable(),
            withField ->
                new FieldVariable(withField.getPojo(), withField.getField(), UNWRAP_OPTIONAL));
  }
}
