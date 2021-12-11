package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.Generators;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.SafeBuilderPojoField;
import java.util.function.ToIntFunction;

/** Factory which creates the classes which forms the SafeBuilder. */
public class SafeBuilderGens {

  private static final String BUILDER_ARGUMENT = "Builder builder";
  private static final String BUILDER_ASSIGNMENT = "this.builder = builder;";

  private SafeBuilderGens() {}

  public static Generator<SafeBuilderPojoField, PojoSettings> fieldBuilderClass() {
    return ClassGen.<SafeBuilderPojoField, PojoSettings>nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(SafeBuilderGens::createClassName)
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
            .className(SafeBuilderGens::createClassName)
            .singleArgument(BUILDER_ARGUMENT)
            .content(BUILDER_ASSIGNMENT);

    return SafeBuilderGens.<SafeBuilderPojoField, PojoSettings>fieldDeclaration()
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
        .className(SafeBuilderGens::createClassName)
        .singleArgument(BUILDER_ARGUMENT)
        .content(BUILDER_ASSIGNMENT);
  }

  public static <A, B> Generator<A, B> andAllOptionalsMethod() {
    return MethodGen.<A, B>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("OptBuilder0")
        .methodName("andAllOptionals")
        .noArguments()
        .content("return new OptBuilder0(builder);");
  }

  public static <A, B> Generator<A, B> andOptionalsMethod() {
    return MethodGen.<A, B>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("andOptionals")
        .noArguments()
        .content("return builder;");
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> p.getName().asString())
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
                f.getField().getName().toPascalCase(),
                f.getField().getName());

    return MethodGen.<SafeBuilderPojoField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SafeBuilderGens::createNextClassName)
        .methodName(f -> String.format("set%s", f.getField().getName().toPascalCase()))
        .singleArgument(
            f ->
                String.format(
                    "%s %s", f.getField().getType().getTypeDeclaration(), f.getField().getName()))
        .content(content)
        .append(RefsGen.fieldRefs(), SafeBuilderPojoField::getField);
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> setMethodOptional() {
    final Generator<SafeBuilderPojoField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(%s.map(builder::set%s).orElse(builder));",
                createNextClassName(f, s),
                f.getField().getName(),
                f.getField().getName().toPascalCase());

    return MethodGen.<SafeBuilderPojoField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SafeBuilderGens::createNextClassName)
        .methodName(f -> String.format("set%s", f.getField().getName().toPascalCase()))
        .singleArgument(
            f ->
                String.format(
                    "Optional<%s> %s",
                    f.getField().getType().getTypeDeclaration(), f.getField().getName()))
        .content(content)
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
        .append(RefsGen.fieldRefs(), SafeBuilderPojoField::getField);
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
        SafeBuilderGens.<Pojo, PojoSettings>fieldDeclaration()
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
        SafeBuilderGens.<Pojo, PojoSettings>fieldDeclaration()
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
