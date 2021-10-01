package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.Generators;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.SafeBuilderPojoField;
import java.util.function.ToIntFunction;

/** Factory which creates the classes which forms the SafeBuilder. */
public class SafeBuilderFactory {

  private static final String BUILDER_ARGUMENT = "Builder builder";
  private static final String BUILDER_ASSIGNMENT = "this.builder = builder;";

  private SafeBuilderFactory() {}

  public static Generator<SafeBuilderPojoField, PojoSettings> fieldBuilderClass() {
    return ClassGen.<SafeBuilderPojoField, PojoSettings>nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(SafeBuilderFactory::createClassName)
        .content(builderClassContent());
  }

  private static String createClassName(SafeBuilderPojoField field, PojoSettings settings) {
    final String prefix = field.getField().isRequired() ? "" : "Opt";
    return String.format("%sBuilder%d", prefix, field.getIndex());
  }

  private static String createNextClassName(SafeBuilderPojoField field, PojoSettings settings) {
    return createClassName(field.withFieldIndex(field.getIndex() + 1), settings);
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> builderClassContent() {
    final Generator<SafeBuilderPojoField, PojoSettings> constructor =
        ConstructorGen.<SafeBuilderPojoField, PojoSettings>modifiers(PRIVATE)
            .className(SafeBuilderFactory::createClassName)
            .singleArgument(BUILDER_ARGUMENT)
            .content(BUILDER_ASSIGNMENT);

    return SafeBuilderFactory.<SafeBuilderPojoField, PojoSettings>fieldDeclaration()
        .append(newLine())
        .append(constructor)
        .append(newLine())
        .append(setMethod())
        .appendConditionally(
            (f, s) -> f.getField().isOptional(),
            Generators.<SafeBuilderPojoField, PojoSettings>newLine().append(setMethodOptional()));
  }

  public static <A, B> Generator<A, B> fieldDeclaration() {
    return (f, s, w) -> w.println("private final Builder builder;");
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> constructor() {
    return ConstructorGen.<SafeBuilderPojoField, PojoSettings>modifiers(PRIVATE)
        .className(SafeBuilderFactory::createClassName)
        .singleArgument(BUILDER_ARGUMENT)
        .content(BUILDER_ASSIGNMENT);
  }

  public static <A, B> Generator<A, B> andAllOptionalsMethod() {
    return MethodGen.<A, B>modifiers(PUBLIC)
        .returnType("OptBuilder0")
        .methodName("andAllOptionals")
        .noArguments()
        .content("return new OptBuilder0(builder);");
  }

  public static <A, B> Generator<A, B> andOptionalsMethod() {
    return MethodGen.<A, B>modifiers(PUBLIC)
        .returnType("Builder")
        .methodName("andOptionals")
        .noArguments()
        .content("return builder;");
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .returnType(p -> p.getPojoName().asString())
        .methodName("build")
        .noArguments()
        .content("return builder.build();");
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> setMethod() {
    final Generator<SafeBuilderPojoField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.set%s(%s));",
                createNextClassName(f, s),
                f.getField().getName().toPascalCase().asString(),
                f.getField().getName().asString());

    return MethodGen.<SafeBuilderPojoField, PojoSettings>modifiers(PUBLIC)
        .returnType(SafeBuilderFactory::createNextClassName)
        .methodName(f -> String.format("set%s", f.getField().getName().toPascalCase().asString()))
        .singleArgument(
            f ->
                String.format(
                    "%s %s",
                    f.getField().getType().getClassName().asString(),
                    f.getField().getName().asString()))
        .content(content);
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> setMethodOptional() {
    final Generator<SafeBuilderPojoField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(%s.map(builder::set%s).orElse(builder));",
                createNextClassName(f, s),
                f.getField().getName().asString(),
                f.getField().getName().toPascalCase().asString());

    return MethodGen.<SafeBuilderPojoField, PojoSettings>modifiers(PUBLIC)
        .returnType(SafeBuilderFactory::createNextClassName)
        .methodName(f -> String.format("set%s", f.getField().getName().toPascalCase().asString()))
        .singleArgument(
            f ->
                String.format(
                    "Optional<%s> %s",
                    f.getField().getType().getClassName().asString(),
                    f.getField().getName().asString()))
        .content(content);
  }

  public static Generator<Pojo, PojoSettings> finalRequiredBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isRequired).size();

    final Generator<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(p -> String.format("Builder%d", builderNumber.applyAsInt(p)))
            .singleArgument(BUILDER_ARGUMENT)
            .content(BUILDER_ASSIGNMENT);

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderFactory.<Pojo, PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(andAllOptionalsMethod())
            .append(newLine())
            .append(andOptionalsMethod())
            .append(newLine())
            .append(buildMethod());

    return ClassGen.<Pojo, PojoSettings>nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className((p, s) -> String.format("Builder%d", builderNumber.applyAsInt(p)))
        .content(content);
  }

  public static Generator<Pojo, PojoSettings> finalOptionalBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isOptional).size();

    final Generator<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(p -> String.format("OptBuilder%d", builderNumber.applyAsInt(p)))
            .singleArgument(BUILDER_ARGUMENT)
            .content(BUILDER_ASSIGNMENT);

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderFactory.<Pojo, PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(buildMethod());

    return ClassGen.<Pojo, PojoSettings>nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className((p, s) -> String.format("OptBuilder%d", builderNumber.applyAsInt(p)))
        .content(content);
  }
}
