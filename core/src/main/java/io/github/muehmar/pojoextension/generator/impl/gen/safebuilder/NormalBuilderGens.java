package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.Generator.emptyGen;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoAndField;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.FieldDeclarationGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.ConstructorCallGens;

/**
 * Factory which creates more or less the well-known standard builder pattern used for the
 * SafeBuilder.
 */
public class NormalBuilderGens {

  private static final String BUILDER_CLASSNAME = "Builder";

  private NormalBuilderGens() {}

  public static Generator<Pojo, PojoSettings> builderClass() {
    final ConstructorGen<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(BUILDER_CLASSNAME)
            .noArguments()
            .content(emptyGen());
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

    return ClassGen.<Pojo, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(p -> BUILDER_CLASSNAME + p.getGenericTypeDeclarationSection())
        .content(content);
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnTypeName(Pojo::getNameWithTypeVariables)
        .methodName("build")
        .noArguments()
        .content(ConstructorCallGens.callWithAllLocalVariables("return "));
  }

  public static Generator<PojoAndField, PojoSettings> setMethod() {
    final Generator<PojoAndField, PojoSettings> content =
        (paf, settings, writer) ->
            writer
                .println("this.%s = %s;", paf.getField().getName(), paf.getField().getName())
                .println("return this;");

    return MethodGen.<PojoAndField, PojoSettings>modifiers(
            (paf, s) ->
                JavaModifiers.of(
                    paf.getField().isRequired() ? JavaModifier.PRIVATE : JavaModifier.PUBLIC))
        .noGenericTypes()
        .returnType(paf -> BUILDER_CLASSNAME + paf.getPojo().getTypeVariablesSection())
        .methodName(paf -> String.format("set%s", paf.getField().getName().toPascalCase()))
        .singleArgument(
            paf ->
                String.format(
                    "%s %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
        .content(content)
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

    return MethodGen.<PojoAndField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(paf -> BUILDER_CLASSNAME + paf.getPojo().getTypeVariablesSection())
        .methodName(paf -> String.format("set%s", paf.getField().getName().toPascalCase()))
        .singleArgument(
            paf ->
                String.format(
                    "Optional<%s> %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
        .content(content)
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
        .append(RefsGen.fieldRefs(), PojoAndField::getField);
  }
}
