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
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.FieldDeclarationGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
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
            .appendList(setMethod().append(newLine()), Pojo::getFields)
            .appendList(
                setMethodOptional().append(newLine()),
                p -> p.getFields().filter(PojoField::isOptional))
            .append(buildMethod());

    return ClassGen.<Pojo, PojoSettings>nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(BUILDER_CLASSNAME)
        .content(content);
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .returnType(p -> p.getName().asString())
        .methodName("build")
        .noArguments()
        .content(ConstructorCallGens.callWithAllLocalVariables());
  }

  public static Generator<PojoField, PojoSettings> setMethod() {
    final Generator<PojoField, PojoSettings> content =
        (field, settings, writer) ->
            writer
                .println("this.%s = %s;", field.getName(), field.getName())
                .println("return this;");

    return MethodGen.<PojoField, PojoSettings>modifiers(
            (f, s) -> JavaModifiers.of(f.isRequired() ? JavaModifier.PRIVATE : JavaModifier.PUBLIC))
        .returnType(BUILDER_CLASSNAME)
        .methodName(f -> String.format("set%s", f.getName().toPascalCase()))
        .singleArgument(f -> String.format("%s %s", f.getType().getClassName(), f.getName()))
        .content(content)
        .append((f, s, w) -> w.ref(f.getType().getQualifiedName().asString()));
  }

  public static Generator<PojoField, PojoSettings> setMethodOptional() {
    final Generator<PojoField, PojoSettings> content =
        (field, settings, writer) ->
            writer
                .println("this.%s = %s.orElse(null);", field.getName(), field.getName())
                .println("return this;");

    return MethodGen.<PojoField, PojoSettings>modifiers(PUBLIC)
        .returnType(BUILDER_CLASSNAME)
        .methodName(f -> String.format("set%s", f.getName().toPascalCase()))
        .singleArgument(
            f -> String.format("Optional<%s> %s", f.getType().getClassName(), f.getName()))
        .content(content)
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
        .append((f, s, w) -> w.ref(f.getType().getQualifiedName().asString()));
  }
}
