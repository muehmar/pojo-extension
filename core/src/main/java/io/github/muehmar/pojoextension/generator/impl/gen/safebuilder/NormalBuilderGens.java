package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.FieldDeclarationGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.ConstructorCallGens;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoAndField;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;

/**
 * Factory which creates more or less the well-known standard builder pattern used for the
 * SafeBuilder.
 */
public class NormalBuilderGens {

  private static final String BUILDER_CLASSNAME = "Builder";

  private NormalBuilderGens() {}

  public static Generator<Pojo, PojoSettings> builderClass() {
    final ConstructorGen<Pojo, PojoSettings> constructor =
        ConstructorGenBuilder.<Pojo, PojoSettings>create()
            .modifiers(PRIVATE)
            .className(BUILDER_CLASSNAME)
            .noArguments()
            .noContent()
            .build();
    final Generator<Pojo, PojoSettings> content =
        constructor
            .append(newLine())
            .appendList(FieldDeclarationGen.ofModifiers(PRIVATE), Pojo::getFields)
            .append(newLine())
            .appendList(setMethod().append(newLine()), Pojo::getPojoAndFields)
            .appendList(
                setMethodOptional().append(newLine()),
                p -> p.getFields().filter(PojoField::isOptional).map(f -> new PojoAndField(p, f)))
            .append(buildMethod());

    return ClassGenBuilder.<Pojo, PojoSettings>create()
        .clazz()
        .nested()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(p -> BUILDER_CLASSNAME + p.getGenericTypeDeclarationSection())
        .noSuperClass()
        .noInterfaces()
        .content(content)
        .build();
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGenBuilder.<Pojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnTypeName(Pojo::getNameWithTypeVariables)
        .methodName("build")
        .noArguments()
        .content(ConstructorCallGens.callWithAllLocalVariables("return "))
        .build();
  }

  public static Generator<PojoAndField, PojoSettings> setMethod() {
    final Generator<PojoAndField, PojoSettings> content =
        (paf, settings, writer) ->
            writer
                .println("this.%s = %s;", paf.getField().getName(), paf.getField().getName())
                .println("return this;");

    return MethodGenBuilder.<PojoAndField, PojoSettings>create()
        .createModifiers(
            (paf, s) ->
                JavaModifiers.of(
                    paf.getField().isRequired() ? JavaModifier.PRIVATE : JavaModifier.PUBLIC))
        .noGenericTypes()
        .returnType(paf -> BUILDER_CLASSNAME + paf.getPojo().getTypeVariablesSection())
        .methodName((paf, s) -> paf.getField().builderSetMethodName(s).asString())
        .singleArgument(
            paf ->
                String.format(
                    "%s %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
        .content(content)
        .build()
        .append(RefsGen.fieldRefs(), PojoAndField::getField);
  }

  public static Generator<PojoAndField, PojoSettings> setMethodOptional() {
    final Generator<PojoAndField, PojoSettings> content =
        (paf, settings, writer) ->
            writer
                .println(
                    "this.%s = %s.orElse(null);",
                    paf.getField().getName(), paf.getField().getName())
                .println("return this;");

    return MethodGenBuilder.<PojoAndField, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(paf -> BUILDER_CLASSNAME + paf.getPojo().getTypeVariablesSection())
        .methodName((paf, s) -> paf.getField().builderSetMethodName(s).asString())
        .singleArgument(
            paf ->
                String.format(
                    "Optional<%s> %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
        .content(content)
        .build()
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
        .append(RefsGen.fieldRefs(), PojoAndField::getField);
  }
}
