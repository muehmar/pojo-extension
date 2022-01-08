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

  private static final String BUILDER_ASSIGNMENT = "this.builder = builder;";

  private SafeBuilderGens() {}

  public static Generator<SafeBuilderPojoField, PojoSettings> fieldBuilderClass() {
    return ClassGen.<SafeBuilderPojoField, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(SafeBuilderGens::classDeclaration)
        .noSuperClassAndInterface()
        .content(builderClassContent())
        .append(RefsGen.genericRefs(), SafeBuilderPojoField::getPojo);
  }

  private static String rawClassName(SafeBuilderPojoField field) {
    final String prefix = field.getField().isRequired() ? "" : "Opt";
    return String.format("%sBuilder%d", prefix, field.getIndex());
  }

  private static String classDeclaration(SafeBuilderPojoField field) {
    return rawClassName(field) + field.getPojo().getGenericTypeDeclarationSection();
  }

  private static String nextClassTypeVariables(SafeBuilderPojoField field) {
    return nextRawClassName(field) + field.getPojo().getTypeVariablesSection();
  }

  private static String nextClassDiamond(SafeBuilderPojoField field) {
    return nextRawClassName(field) + field.getPojo().getDiamond();
  }

  private static String nextRawClassName(SafeBuilderPojoField field) {
    return rawClassName(field.withIndex(field.getIndex() + 1));
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> builderClassContent() {
    return SafeBuilderGens.<PojoSettings>fieldDeclaration()
        .contraMap(SafeBuilderPojoField::getPojo)
        .append(newLine())
        .append(constructor())
        .append(newLine())
        .append(setMethod())
        .appendConditionally(
            (f, s) -> f.getField().isOptional(),
            Generators.<SafeBuilderPojoField, PojoSettings>newLine().append(setMethodOptional()));
  }

  public static <A> Generator<Pojo, A> fieldDeclaration() {
    return (p, s, w) -> w.println("private final Builder%s builder;", p.getTypeVariablesSection());
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> constructor() {
    return ConstructorGen.<SafeBuilderPojoField, PojoSettings>modifiers(PRIVATE)
        .className(SafeBuilderGens::rawClassName)
        .singleArgument(
            f -> String.format("Builder%s builder", f.getPojo().getTypeVariablesSection()))
        .content(BUILDER_ASSIGNMENT);
  }

  public static <A> Generator<Pojo, A> andAllOptionalsMethod() {
    return MethodGen.<Pojo, A>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> "OptBuilder0" + p.getTypeVariablesSection())
        .methodName("andAllOptionals")
        .noArguments()
        .content(p -> String.format("return new OptBuilder0%s(builder);", p.getDiamond()));
  }

  public static <A> Generator<Pojo, A> andOptionalsMethod() {
    return MethodGen.<Pojo, A>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> "Builder" + p.getTypeVariablesSection())
        .methodName("andOptionals")
        .noArguments()
        .content("return builder;");
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> p.getName().asString() + p.getTypeVariablesSection())
        .methodName("build")
        .noArguments()
        .content("return builder.build();");
  }

  public static Generator<SafeBuilderPojoField, PojoSettings> setMethod() {
    final Generator<SafeBuilderPojoField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.%s(%s));",
                nextClassDiamond(f), f.getField().builderSetMethodName(s), f.getField().getName());

    return MethodGen.<SafeBuilderPojoField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SafeBuilderGens::nextClassTypeVariables)
        .methodName((f, s) -> f.getField().builderSetMethodName(s).asString())
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
                "return new %s(%s.map(builder::%s).orElse(builder));",
                nextClassDiamond(f), f.getField().getName(), f.getField().builderSetMethodName(s));

    return MethodGen.<SafeBuilderPojoField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SafeBuilderGens::nextClassTypeVariables)
        .methodName((f, s) -> f.getField().builderSetMethodName(s).asString())
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
            .singleArgument(p -> String.format("Builder%s builder", p.getTypeVariablesSection()))
            .content(BUILDER_ASSIGNMENT);

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderGens.<PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(andAllOptionalsMethod())
            .append(newLine())
            .append(andOptionalsMethod())
            .append(newLine())
            .append(buildMethod());

    return ClassGen.<Pojo, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(
            (p, s) ->
                String.format(
                    "Builder%d%s",
                    builderNumber.applyAsInt(p), p.getGenericTypeDeclarationSection()))
        .noSuperClassAndInterface()
        .content(content)
        .append(RefsGen.genericRefs());
  }

  public static Generator<Pojo, PojoSettings> finalOptionalBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isOptional).size();

    final Generator<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(p -> String.format("OptBuilder%d", builderNumber.applyAsInt(p)))
            .singleArgument(p -> String.format("Builder%s builder", p.getTypeVariablesSection()))
            .content(BUILDER_ASSIGNMENT);

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderGens.<PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(buildMethod());

    return ClassGen.<Pojo, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(
            (p, s) ->
                String.format(
                    "OptBuilder%d%s",
                    builderNumber.applyAsInt(p), p.getGenericTypeDeclarationSection()))
        .noSuperClassAndInterface()
        .content(content)
        .append(RefsGen.genericRefs());
  }
}
